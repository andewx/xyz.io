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

  handleDelete(event){
  event.preventDefault();
  }

  handleEdit(event){
   var id = event.target.getAttribute('uid');
    $("#"+id).toggle();
  }

  handleSave(event){
  event.preventDefault();
  var userID = event.target.getAttribute('refUser');
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
        <div className="itemButton pull-right" refUser={user.UID} onClick={this.handleSave}>Save</div>
    </div>
    </div>
   </li>
    );
    if (error) {
      return <div className="row"><h3>Users</h3><a className="fa-plus fa pull-right"></a><input type="text" maxLength="45" value={this.state.searchName} onInput={this.onUserSearch}/></div>
    } else if (!isLoaded) {
      return <div className="row"><h3>Users</h3> <a className="fa-plus fa pull-right"></a><input type="text" maxLength="45" value={this.state.searchName} onInput={this.onUserSearch}/></div>
    } else {
         return (
        <div>
        <div className="row"><h3>Users</h3><a className="fa-plus fa pull-right"></a> <input type="text" maxLength="45" value={this.state.searchName} onInput={this.onUserSearch}/></div>
         <ul>{listItems}</ul>
         </div>
         );
       }
    }
 }

  ReactDOM.render(
  <UsersSearch/>, document.getElementById('rootUsers')
  );