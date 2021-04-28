
class RenderOptions extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            error: null,
            isLoaded: false,
            themes: []
        }
    }

    componentDidMount(){
      var url = "/theme/getKeys"
      fetch("/theme/getKeys")
           .then(res => res.json())
           .then(
             (result) => {
               this.setState({
                 isLoaded: true,
                 themes: result
               });
             },
             // Note: it's important to handle errors here
             // instead of a catch() block so that we don't swallow
             // exceptions from actual bugs in components.
             (error) => {
               this.setState({
                 isLoaded: true,
                 error: true,
                 themes: []
               });
             }
           )

    }

    render(){
        const {error, loaded, themes} = this.state;
            console.log(themes);

            if(themes !== []){
            return(
              <div className="itemField"><label>Themes</label> <select name="Theme">
                     { themes.map(theme =>(
                        <option key={theme} name={theme} value={theme} required>{theme}</option>
                     ))}
                     </select>
              </div>
            )
            }else{
               return (<div><progress></progress></div>)
            }
        }
}


class SiteForm extends React.Component{
    constructor(props){
     super(props);
      this.state = {
        error: false,
        isLoaded: false,
        success: false,
        themes: [],
        hidden: true
      }
       this.handleGroupSubmit = this.handleGroupSubmit.bind(this);
       this.setHidden = this.setHidden.bind(this);
    }

     handleGroupSubmit(e){
           e.preventDefault();
            var formData = new FormData(e.target);

           $("#siteForm").toggle();
           $.ajax({
            url: "/site/addSite",
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
            success: function(data, status, jqXHR){
                $('.message_success').toggle();
                location.reload();
            },
            error: function(jqXHR, status, errorTh){
            }
           });

     }

  componentDidMount(){

          this.setState({
                    isLoaded: true
        })
   }

   setHidden(hide){
    this.setState({hidden:hide})
   }

    render(){
     const {error, isLoaded, success, formData, hidden} = this.state;

     if(error){
       return <div className="error">Error Loading Group Form</div>
     }else if (!isLoaded){
       return <div className="pace"><div className="pace-progress"></div></div>
     }else if (!hidden){

      return (
                 <div id="siteForm" className="dash-element-2">
                    <button onClick={() => this.setHidden(!hidden)}>+</button>
                     <h3>Create Site</h3>
                     <div className="message-success is-hidden">Site Created</div>
                     <div className="message-error is-hidden">Error Submitting Site</div>
                     <form id='site-form' onSubmit={this.handleGroupSubmit} value='Submit'  encType='multipart/form-data'>
                         <div className="itemField"><div className='itemLabel'><label>Site Name</label></div> <input id="site-name"  maxLength='45' type='text' name='Name' required/></div>
                         <div className="itemField"><div className='itemLabel'><label>Title</label></div> <input id="site-title"  maxLength='45' type='text' name='Title' required/></div>
                         <div className="itemField"><div className='itemLabel '><label>Description</label></div> <textarea  id="site-desc" name='Description' required/></div>
                         <div className="itemField"><div className='itemLabel '><label>Url</label></div> <input  maxLength='45' type='text' id="site-url" name='RestURL' required/></div>
                         <RenderOptions/>
                         <div className="itemField"><input className="itemButton" type="submit" name="Submit" value="Submit"/></div>
                     </form>
                 </div>
             );
        }
      else{
        return ( <div><button onClick={()=>this.setHidden(!hidden)}>+</button></div>)
      }
     }
}

function Sites(){
    const [request, setRequest] = React.useState({items:[], loaded:false});
    let mounted = true
    const url ="/site/getKeys"

    React.useEffect(() => {
         let mounted = true
               async function load(){
                   const res = await fetch(url);
                   const result = await res.json();
                   if(mounted){
                       setRequest({items: result, loaded:true});
                   }
               }
               load();
               return function cleanup(){mounted = false}
        },[])

    function renderSites(load, list){

        if(load){
            return(
            <div>
                <div className="dash-section"><SiteForm></SiteForm></div>
                <div className="dash-section align-center">
                {list.map((item,index) =>(
                    <Site key={item} name={item} index={index} ></Site>
                ))}

                </div>
            </div>
             )
        }else{
            return <div></div>
        }
    }

    return (
        renderSites(request.loaded, request.items)
    )

  }

  function Site(props){
   const [name, setName] = React.useState(props.name);
   const [index, setIndex] = React.useState(props.index);
   const [request, setRequest] = React.useState({item: null, theme: null, loaded: false});
   const [showDir, setShowDir] = React.useState(false);
   const [showEdit, setShowEdit] = React.useState(false);
   const [showForm, setShowForm] = React.useState(false);
   const formRef = React.useRef(null);
   const pageFormRef = React.useRef(null);

   React.useEffect(() =>{
        var url = "/site/getSite/"+name
        let mounted = true
        async function load(){
            const res = await fetch(url);
            const result = await res.json();
            console.log(result)
            const themeRes = await fetch("theme/get/"+ result.ThemeID);
            const themeResult = await themeRes.json();
            if(mounted){
                setRequest({item: result, theme:themeResult, loaded:true});
            }
        }
        load();
        return function cleanup(){mounted = false}
    }, []);

  var convertRgbCss = function(rgb){
       var format =  "rgb("+rgb[0]+","+ rgb[1] + "," + rgb[2] + ")"
       console.log(format)
       return format
   }

  function submit(){
           var siteForm = formRef.current
           var formData = new FormData(siteForm);
           var url = '/site/addFiles/'+name;
           $.ajax(url, {
                     type: 'POST',
                     data: formData,
                     cache: false,
                     processData: false,
                     contentType: false
                 }).done(function(){
                     window.location.reload();
                 });

    }

      function submitPage(){
               var siteForm = pageFormRef.current
               var formData = new FormData(siteForm);
               var url = '/site/addPage/'+name;
               $.ajax(url, {
                         type: 'POST',
                         data: formData,
                         cache: false,
                         processData: false,
                         contentType: false
                     }).done(function(){
                         window.location.reload();
                     });

        }


   function renderDirs(uid, directory){
        var dirKeys = Object.keys(directory)
        var dirRef = directory
        var name = uid


        if(showDir){
        const dirs = (dirKeys.map((dir,index) =>(
            <ul key={dir}  onClick={() =>{
            $("#dir-"+uid+index).toggle();
            $("#arr-"+uid+index).toggleClass("fa-angle-right");
            $("#arr-"+uid+index).toggleClass("fa-angle-down");
            }}>
            <div id={"arr-"+uid+index} className="fa fa-angle-right" style={{padding:"10px"}} ></div>

            {dir}<div className="is-hidden" id={"dir-"+uid+index} >{renderDirFiles(dirRef[dir], name)}</div>
            </ul>
        )));
       return dirs
       }else{ return(<div></div>)}
   }

   function renderDirFiles(dirObj, uid){
   try{
      let fileKeys = Object.keys(dirObj)
      const files = (fileKeys.map(file =>(
        <li key={file}>{file} <div onClick={() => deleteFile(dirObj[file])}className="fa fa-trash"></div></li>
      )));
      return files;
     }
      catch(error){
        console.log("Can't covert dir obj")
        console.log(error)
      }
   }

   function showDirFiles(dir,index){
        console.log("toggling...")
        $("#dir-"+dir+index).toggle()
   }


  async function deleteItem(name){
        const res = await fetch("/site/delete/"+name);
        location.reload();
  }

  async function deleteFile(f){

       var formData = {'file' : f}
       const res = await fetch("/site/deleteFile/"+name,{
           method: 'POST',
           body: JSON.stringify(formData),
           cache: 'no-cache'
       });
        console.log(res)
        window.location.reload();
  }

   function renderEdit(){
     if(showEdit){
         return (
             <div id={request.item.UID+"-fileForm"} className="internal-block">
                   <div id={request.item.UID+"-edit"}><form ref={formRef} name="file-form" encType="multipart/form-data">
                    <input type="file" name="files"  multiple/>
                    </form>
                    <button onClick={submit}>Upload</button>
                    </div>
             </div>
         );
         }else{
           return <div></div>
         }
   }

   function renderForm(){
    if(showForm){
             return (
                 <div id={request.item.UID+"-pageForm"} className="internal-block">
                       <div id={request.item.UID+"-edit"}><form ref={pageFormRef} name="html-form" encType="multipart/form-data">
                        <label>Page Name</label><input type="text" name="page" required></input><br></br>
                        </form>
                        <button onClick={submitPage}>Submit</button>
                        </div>
                 </div>
             );
             }else{
               return <div></div>
             }
   }

   function renderFolder(){
    if(!showDir){
        return(
            <div className="fa fa-folder pull-left style='padding:10px; margin-bottom:10px;'" onClick={()=>setShowDir(!showDir)}></div>
        )
    }
   return(
               <div className="fa fa-folder-open pull-left style='padding:10px; margin-bottom:10px;'" onClick={()=>setShowDir(!showDir)}></div>
       )
   }


   function renderItem(load, index){
        if(load){
            return(
             <div id={request.item.UID} className="dash-element-3 item selectable-light" id={request.item.UID} key={request.item.UID}>
                                  <div className="fa fa-trash pull-right"  onClick={() => deleteItem(request.item.UID)}></div>
                                  <div className="fa fa-upload pull-right"  onClick={() => setShowEdit(!showEdit)}></div>
                                  <div className="fa fa-plus pull-right"  onClick={() => setShowForm(!showForm)}></div>
                                  <br></br>
                                  {renderForm()}
                                  {renderEdit()}
                                  <br></br>
                                  <div className="palette-banner" style={{background : convertRgbCss(JSON.parse(request.theme.Palette)[0])}}></div>
                                  <div className="palette-banner" style={{background : convertRgbCss(JSON.parse(request.theme.Palette)[1])}}></div>
                                  <div className="palette-banner" style={{background : convertRgbCss(JSON.parse(request.theme.Palette)[2])}}></div>
                                  <div className="palette-banner" style={{background : convertRgbCss(JSON.parse(request.theme.Palette)[3])}}></div>
                                  <div className="palette-banner" style={{background : convertRgbCss(JSON.parse(request.theme.Palette)[4])}}></div>
                                   <h3 className="text-center"><a href={"/site/editSite/"+ request.item.UID + "/Main"}>{request.item.Name}</a></h3><div className="caption">{request.item.Description}</div>
                              {renderFolder()}
                              <br></br>
                              {renderDirs(request.item.UID, JSON.parse(request.item.Dir))}
                    </div>
            )
        }
        else{
            return (<div></div>)
        }
   }


    return(
        renderItem(request.loaded)
    )
  }

  ReactDOM.render(
   <Sites/>, document.getElementById('rootSites')
   );
