class SiteForm extends React.Component{
    constructor(props){
     super(props);
      this.state = {
        error: false,
        isLoaded: false,
        success: false
      }
       this.handleGroupSubmit = this.handleGroupSubmit.bind(this);
    }

     handleGroupSubmit(e){
           e.preventDefault();
            var formData = new FormData(e.target);

           $("#siteForm").toggle();
           $.ajax({
            url: "/model/create/Site",
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
        });
     }


    render(){
     const {error, isLoaded, success, formData} = this.state;

     if(error){
       return <div className="error">Error Loading Group Form</div>
     }else if (!isLoaded){
       return <div className="pace"><div className="pace-progress"></div></div>
     }else{
      return (
                 <div id="siteForm" className="is-hidden">
                     <h3>Create Group</h3>
                     <div className="message-success is-hidden">Site Created</div>
                     <div className="message-error is-hidden">Error Submitting Site</div>
                     <form id='group-form' onSubmit={this.handleGroupSubmit} value='Submit'  encType='multipart/form-data'>
                         <div className="itemField"><div className='itemLabel'>Site Name</div> <input id="site-name"  maxLength='45' type='text' name='Name' required/></div>
                         <div className="itemField"><div className='itemLabel'>Title</div> <input id="site-title"  maxLength='45' type='text' name='Title' required/></div>
                         <div className="itemField"><div className='itemLabel '>Description</div> <textarea  id="site-desc" name='Description' required/></div>
                         <div className="itemField"><div className='itemLabel '>Description</div> <input  maxLength='45' type='text' id="site-url" name='RestURL' required/></div>
                         <div className="itemField"><input className="itemButton" type="submit" name="Submit" value="Submit"/></div>
                     </form>
                 </div>
             );
     }
    }
}


class ReactSites extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      error: null,
      isLoaded: false,
      items: []
    };

    this.showForm = this.showForm.bind(this);
  }

  componentDidMount() {
    fetch("/sites/get")
      .then(res => res.json())
      .then(
        (result) => {
          this.setState({
            isLoaded: true,
            items: result
          });
        },
        // Note: it's important to handle errors here
        // instead of a catch() block so that we don't swallow
        // exceptions from actual bugs in components.
        (error) => {
          this.setState({
            isLoaded: true,
            error,
            items: []
          });
        }
      )
  }

  showForm(){
   $("#siteForm").toggle();
  }

  render() {
    const { error, isLoaded, items } = this.state;
    if (error) {
      return <div>Error: {error.message}</div>;
    } else if (!isLoaded) {
      return <div>Loading...</div>;
    } else {
      return (
      <div>
        <div className="section">
        <div className="dash-button pull-right" onClick={this.showForm}><div className="vertical-center align-center text-center"><div className="fa-plus fa"></div></div></div>
        </div>
        <div className="section">
        <SiteForm></SiteForm>
        </div>

        <div className="dash-section">
          {items.map(item => (
            <div className="dash-element-3 item selectable-light" key={item.UID}>
              <h3 className="text-center">{item.Name}</h3> <h4>{item.Title}</h4><div className="caption">{item.Description}</div>
            </div>
          ))}
        </div>
        </div>
      );
    }
  }
}


  ReactDOM.render(
   <ReactSites/>, document.getElementById('rootSites')
   );