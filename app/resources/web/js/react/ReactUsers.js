 class UserForm extends React.Component{
    constructor(props){
     super(props);
      this.state = {
        error: false,
        isLoaded: false,
        success: false,
        formData: {},
        groups: []
      }

       this.handleSubmit = this.handleSubmit.bind(this);
    }

    ajaxGroups(){
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

    handleSubmit(e){
       e.preventDefault();
       this.formData['Email'] = $("#user-email").getValue();
       this.formData['Name'] = $("#user-name").getValue();
       this.formData['FirstName'] = $("#user-first").getValue();
       this.formData['LastName'] = $("#user-last").getValue();
       this.formData['Password'] = $("#user-password").getValue();
       this.formData['GroupID'] = $("#user-group").getValue();
       $("formUser").toggle();
       $.post("/model/create/User", this.formData).done(function(data){
        $('.message-success').html("User created!");
       });



     }

     componentDidMount(){
        this.ajaxGroups();
     }

    render(){
    const {error, isLoaded, success, formData, groups} = this.state;
     const listGroups = this.state.groups.map((group) =>
        <option key={group.UID} value={group.UID}>{group.UID}</option>
        );

     if(error){
        return <div className="error">Error submitting user</div>
     }else if (!isLoaded){
        return <div className="pace"><div className="pace-progress"></div></div>
     }else{
        return (
            <div id="userForm" className="is-hidden">
                <h3>Create User</h3>
                <div className="message-success is-hidden"></div>
                <div className="message-error is-hidden"></div>
                <form id='user-form' action='/model/create/User' method='post' value='Submit'  encType='multipart/form-data'>
                    <div className="itemField"><div className='itemLabel'>Email:</div> <input id="user-email"  maxLength='45' type='email' name='Email' required/></div>
                    <div className="itemField"><div className='itemLabel '>Username</div> <input id="user-name"  maxLength='45' type='text' name='Name' required/></div>
                    <div className="itemField"><div className='itemLabel '>First:</div> <input id= "user-first"  maxLength='45' type='text' name='FirstName' required/></div>
                    <div className="itemField"><div className='itemLabel '>Last:</div> <input id ="user-last"  maxLength='45' type='text' name='LastName' required/></div>
                    <div className="itemField"><div className='itemLabel '>Password:</div> <input id="user-pass"  maxLength='45' type='password' name='Password' required/></div>
                    <div className="itemField"><div className='itemLabel '>Group:</div> <select id="user-group" name="GroupID"  name="GroupID">{listGroups}</select></div>
                    <div className="itemField"><input className="itemButton" type="submit" name="Submit" value="Submit" onSubmit={this.handleSubmit}/></div>
                </form>
            </div>
        );
     }
    }
 }


class UsersSearch extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      error: null,
      isLoaded: false,
      users: [],
      groups: [],
      searchName: ""
    };
    this.onUserSearch = this.onUserSearch.bind(this);
    this.handleDelete = this.handleDelete.bind(this);
    this.handleEdit = this.handleEdit.bind(this);
    this.handleSave = this.handleSave.bind(this);
    this.handleChange = this.handleChange.bind(this);
    this.handleForm = this.handleForm.bind(this);
  }

  onUserSearch(event){
    this.setState( state => ({
        searchName: event.target.value
    }));
    if(this.state.searchName != ""){
        this.ajaxFind();
        event.target.focus();
     }
  }

  handleForm(e){
   e.preventDefault();
   $("#userForm").toggle();
  }

  handleDelete(event){
  event.preventDefault();
  }

  handleEdit(event){
   var id = event.target.getAttribute('uid');
    $("#"+id).toggle();
  }

  handleSave(index, event){
  event.preventDefault();
  Pace.restart();
  const userObj = this.state.users[index];
  $.post("/model/update/User/" + userObj.UID, userObj)
  }

  componentDidMount(){
    this.ajaxGroups();
  }



  ajaxFind(){
   fetch("/users/find/" + this.state.searchName)
        .then(res => res.json())
        .then(
          (result) => {
            this.setState({
              isLoaded: true,
              users: result
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

    ajaxGroups(){
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

    handleChange(index, e){
     var prop = e.target.getAttribute("name")
     var val = e.target.value
     this.setState(state => {
     const users = state.users.map((user, j) =>{

      if(j === index){
        user[prop] = val;

        return user;
      }else{
        return user;
      }
     });
     return {users, };
     });
   }


  render() {
    const { error, isLoaded, users, groups, searchName } = this.state;
    const listGroups = groups.map((group) =>
    <option key={group.UID} value={group.UID}>{group.UID}</option>
    );

    const listItems = users.map((user, index) =>
    <li key={user.UID}><div className="item">
    <div className="itemName">{user.Name}</div>
    <div className="itemIcons pull-right"><a href="#" onClick={this.handleDelete} className="fa-minus fa"></a><a href="#" className="fa-edit fa" onClick={this.handleEdit} uid={user.Name}></a></div>
    <div className="itemCaption">{user.Email}</div>
    <div id={user.Name} className="itemFields">
        <div className="itemField"><div className="itemLabel">Username</div><input type="text" onChange={this.handleChange.bind(this, index)} name="Name" value={user.Name}/></div>
        <div className="itemField"><div className="itemLabel">First</div><input type="text"  onChange={this.handleChange.bind(this, index)} name="FirstName" value={user.FirstName}/></div>
        <div className="itemField"><div className="itemLabel">Last</div><input type="text"  onChange={this.handleChange.bind(this, index)} name="LastName" value={user.LastName}/></div>
        <div className="itemField"><div className="itemLabel">Group</div><select name="GroupID"  onChange={this.handleChange.bind(this, index)} name="GroupID" value={user.GroupID}>{listGroups}</select></div>
        <div className="itemButton pull-right" onClick={this.handleSave.bind(this, index)}>Save</div>
    </div>
    </div>
   </li>
    );
    if (error) {
      return <div className="row"><div className="pace"><div className="pace-progress"></div></div><h3>Users</h3><a className="fa-plus fa pull-right" onClick={this.handleForm}></a><input type="text" maxLength="45" value={this.state.searchName} onInput={this.onUserSearch}/><UserForm></UserForm></div>
    } else if (!isLoaded) {
      return <div className="row"><div className="pace"><div className="pace-progress"></div></div><h3>Users</h3> <a className="fa-plus fa pull-right" onClick={this.handleForm}></a><input type="text" maxLength="45" value={this.state.searchName} onInput={this.onUserSearch}/><UserForm></UserForm></div>
    } else {
         return (
        <div>
        <div className="row"><div className="pace"><div className="pace-progress"></div></div><h3>Users</h3><a  className="fa-plus fa pull-right" onClick={this.handleForm}></a> <input type="text" maxLength="45" value={this.state.searchName} onInput={this.onUserSearch}/><UserForm></UserForm></div>
        <div id="UserFormHook"></div>
         <ul>{listItems}</ul>
         </div>
         );
       }
    }
 }



  ReactDOM.render(
  <UsersSearch/>, document.getElementById('rootUsers')
  );
