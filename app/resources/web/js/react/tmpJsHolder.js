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
      var url = "/themes/getKeys"
      fetch("/themes/getKeys")
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
        themes: []
      }
       this.handleGroupSubmit = this.handleGroupSubmit.bind(this);
    }

     handleGroupSubmit(e){
           e.preventDefault();
            var formData = new FormData(e.target);

           $("#siteForm").toggle();
           $.ajax({
            url: "/sites/addSite",
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




    render(){
     const {error, isLoaded, success, formData} = this.state;

     if(error){
       return <div className="error">Error Loading Group Form</div>
     }else if (!isLoaded){
       return <div className="pace"><div className="pace-progress"></div></div>
     }else{
      return (
                 <div id="siteForm" className="dash-element-2 is-hidden">
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
    }
}