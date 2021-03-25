
class GroupForm extends React.Component{
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

           $("#groupForm").toggle();
           $.ajax({
            url: "/model/create/Group",
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
                 <div id="groupForm" className="is-hidden">
                     <h3>Create Group</h3>
                     <div className="message-success is-hidden"></div>
                     <div className="message-error is-hidden"></div>
                     <form id='group-form' onSubmit={this.handleGroupSubmit} value='Submit'  encType='multipart/form-data'>
                         <div className="itemField"><div className='itemLabel'>Group Name</div> <input id="group-name"  maxLength='45' type='text' name='GroupID' required/></div>
                         <div className="itemField"><div className='itemLabel '>Description</div> <textarea  id="group-desc" name='AccessDescription' required/></div>
                         <div className="itemField"><div className='itemLabel '>Access Level</div> <input  id= "group-level"  min='1' max='5' type='number' name='AccessLevel' required/></div>
                         <div className="itemField"><input className="itemButton" type="submit" name="Submit" value="Submit"/></div>
                     </form>
                 </div>
             );
     }



    }
}

class GroupsComponent extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      error: null,
      isLoaded: false,
      groups: []
    };

 this.handleChange = this.handleChange.bind(this);
 this.handleSave = this.handleSave.bind(this);
 this.handleEdit = this.handleEdit.bind(this);
 this.handleDelete = this.handleDelete.bind(this);
 this.showForm = this.showForm.bind(this);
 this.fetchGroups = this.fetchGroups.bind(this);
 this.refresh = this.refresh.bind(this);

  }

  fetchGroups(){
   fetch("/groups/list")
        .then(res => res.json())
        .then(
          (result) => {
            this.setState({
              isLoaded: true,
              groups: result
            });
          },
          (error) => {
            this.setState({
              isLoaded: true,
              error
            });
          }
        )
  }

  componentDidMount(){
    this.fetchGroups();
  }

   handleSave(index, event){
    event.preventDefault();
    const groupObj = this.state.groups[index];
    Pace.restart();
    $.post("/model/update/Group/" + groupObj.UID, groupObj)
    }

    handleDelete(index, event){
      var ref = this;
      event.preventDefault();
      const groupObj = this.state.groups[index];
      $.ajax({
        url: "/model/delete/Group/"+ groupObj.UID,
        success: function(result){
            ref.fetchGroups();
        }
      });
    }




      handleEdit(event){
       var id = event.target.getAttribute('uid');
        $("#"+id).toggle();
      }

  handleChange(index, e){
       var prop = e.target.getAttribute("name")
       var val = e.target.value
       this.setState(state => {
       const groups = state.groups.map((group, j) =>{

        if(j === index){
          group[prop] = val;

          return group;
        }else{
          return group;
        }
       });
       return {groups, };
       });
     }

     showForm(e){
        e.preventDefault();
        $("#groupForm").toggle();

     }

    refresh(e){
       e.preventDefault();
       this.fetchGroups();
    }

  render() {

    const { error, isLoaded, groups, searchName } = this.state;
    const listItems = groups.map((group, index) =>
     <li key={group.UID}><div className="item">
        <div className="itemName">{group.Name}</div>
        <div className="itemIcons pull-right"><a href="#" onClick={this.handleDelete.bind(this,index)} className="fa-minus fa"></a><a href="#" uid={group.UID} className="fa-edit fa" onClick={this.handleEdit}></a></div>
        <div className="itemCaption">{group.AccessDescription}</div>
        <div id={group.UID} className="itemFields">
            <div className="itemField"><div className="itemLabel">Name</div><input type="text" name="GroupID" value={group.GroupID} readOnly={true}/></div>
            <div className="itemField"><div className="itemLabel">Level</div><input type="number"  onChange={this.handleChange.bind(this, index)} min="1" max="6" name="AccessLevel" value={group.AccessLevel}/></div>
            <div className="itemField"><div className="itemLabel">Description</div><textarea type="text"  onChange={this.handleChange.bind(this, index)} name="AccessDescription" value={group.AccessDescription}/></div>
            <div className="itemButton pull-right" onClick={this.handleSave.bind(this, index)}>Save</div>
        </div>
        </div>
       </li>
    );
    if (error) {
      return <div><div className="row"><h3>Groups</h3><a className="fa-refresh fa pull-right" onClick={this.refresh}></a><a className="fa-plus fa pull-right" onClick={this.showForm}></a></div><GroupForm></GroupForm></div>
    } else if (!isLoaded) {
      return <div><div className="row"><h3>Groups</h3><a className="fa-refresh fa pull-right" onClick={this.refresh}></a><a className="fa-plus fa pull-right" onClick={this.showForm}></a></div><GroupForm></GroupForm></div>
    } else {
         return (
        <div>
        <div className="row"><h3>Groups</h3><a className="fa-refresh fa pull-right" onClick={this.refresh}></a><a className="fa-plus fa pull-right" onClick={this.showForm}></a></div><GroupForm></GroupForm>
            <ul>{listItems}</ul>
         </div>
         );
       }
    }
 }

   ReactDOM.render(
   <GroupsComponent/>, document.getElementById('rootGroups')
   );