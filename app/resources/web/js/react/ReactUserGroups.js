class UsersSearch extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      error: null,
      isLoaded: false,
      users: [],
      searchName: ""
    };
    this.onUserSearch = this.onUserSearch.bind(this);
  }

  onUserSearch(event){
    this.setState( state => ({
        searchName: event.target.value
    }));
    if(this.state.searchName != ""){
        this.ajax();
        event.target.focus();
     }
  }


  ajax(){
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


  render() {

    const { error, isLoaded, users, searchName } = this.state;
    const listItems = users.map((user) =>
    <li key={user.UID}>{user.Email}</li>
    );
    if (error) {
      return <div className="row"><h3>Search Users:</h3> <input type="text" maxLength="45" value={this.state.searchName} onInput={this.onUserSearch}/></div>
    } else if (!isLoaded) {
      return <div className="row"><h3>Search Users:</h3> <input type="text" maxLength="45" value={this.state.searchName} onInput={this.onUserSearch}/></div>
    } else {
         return (
        <div>
        <div className="row"><h3>Search Users:</h3> <input type="text" maxLength="45" value={this.state.searchName} onInput={this.onUserSearch}/></div>
         <ul>{listItems}</ul>
         </div>
         );
       }
    }
 }

  ReactDOM.render(
  <UsersSearch/>, document.getElementById('rootUsers'));