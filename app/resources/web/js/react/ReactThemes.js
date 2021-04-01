function Themes(){
    const [request, setRequest] = React.useState({items:[], loaded:false});
    let mounted = true
    const url ="/themes/getKeys"

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

    function renderThemes(load, list){

        if(load){
            return(
            <div>
                <div className="dash-section align-center">
                {list.map(item =>(
                    <Theme key={item} name={item} ></Theme>
                ))}

                </div>
            </div>
             )
        }else{
            return <div></div>
        }
    }

    return (
        renderThemes(request.loaded, request.items)
    )

  }

  function Theme(props){
   const [name, setName] = React.useState(props.name);
   const [request, setRequest] = React.useState({item: null, loaded: false});
   const [showDir, setShowDir] = React.useState(false);
   const [showEdit, setShowEdit] = React.useState(false);

   React.useEffect(() =>{
        var url = "/themes/get/"+name
        let mounted = true
        async function load(){
            const res = await fetch(url);
            const result = await res.json();
            if(mounted){
                setRequest({item: result, loaded:true});
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
        <li key={file}>{file} <div className="fa fa-trash"></div></li>
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
        const res = await fetch("/themes/delete/"+name);
        location.reload();
  }

  async function deleteFile(name, file){
       const res = await fetch("/themes/deleteFile/"+name+"/"+file);
       location.reload();
  }

   function renderEdit(){
     if(showEdit){
         return (
             <div id={request.item.UID+"-dir"} className="internal-block">
                   <div id={request.item.UID+"-edit"}><form encType="multipart/form-data"><input type="file" name="files"  multiple/></form></div>
             </div>
         );
         }else{
           return <div></div>
         }
   }

   function renderFolder(){
    if(!showDir){
        return(
            <div className="fa fa-folder pull-left style='padding:10px; margin-bottom:10px;'" onClick={() => setShowDir(!showDir)}></div>
        )
    }
   return(
               <div className="fa fa-folder-open pull-left style='padding:10px; margin-bottom:10px;'" onClick={() => setShowDir(!showDir)}></div>
       )
   }


   function renderItem(load){
        if(load){
            return(
             <div id={request.item.UID} className="dash-element-3 item selectable-light" id={request.item.UID} key={request.item.UID}>
                                  <div className="fa fa-trash pull-right"  onClick={() => deleteItem(request.item.UID)}></div>
                                  <div className="fa fa-upload pull-right"  onClick={() => setShowEdit(!showEdit)}></div>
                                  <br></br>
                                  {renderEdit()}
                                  <br></br>
                                  <div className="palette-banner" style={{background : convertRgbCss(JSON.parse(request.item.Palette)[0])}}></div>
                                  <div className="palette-banner" style={{background : convertRgbCss(JSON.parse(request.item.Palette)[1])}}></div>
                                  <div className="palette-banner" style={{background : convertRgbCss(JSON.parse(request.item.Palette)[2])}}></div>
                                  <div className="palette-banner" style={{background : convertRgbCss(JSON.parse(request.item.Palette)[3])}}></div>
                                  <div className="palette-banner" style={{background : convertRgbCss(JSON.parse(request.item.Palette)[4])}}></div>
                                   <h3 className="text-center">{request.item.Name}</h3><div className="caption">{request.item.HtmlFile}</div>
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
   <Themes/>, document.getElementById('rootTheme')
   );
