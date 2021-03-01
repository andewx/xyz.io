class UsersSearch extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      error: null,
      isLoaded: false,
      users: {},
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
     }
  }

  ajax(){
   fetch("/users/find/" + this.state.searchName)
        .then(res => res.json())
        .then(
          (result) => {
            this.setState({
              isLoaded: true,
              users: JSON.parse(result)
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
    if (error) {
      return <div className="row">Search Users: <input type="text" maxLength="45" onInput={this.onUserSearch}/></div>;
    } else if (!isLoaded) {
      return <div className="row">Search Users: <input type="text" maxLength="45" onInput={this.onUserSearch}/></div>;
    } else {
        if (typeof(users) != "undefined"){
         return (
        <div>
         <div className="row">Search Users: <input type="text" maxLength="45" oninput={this.onUserSearch}/></div>
          <div className="row">
              <div className="col-1-3">Email</div>
              <div className="col-1-3">Username</div>
              <div className="col-1-3">Belongs To(Group)</div>
          </div>

          {users.map(item => (
          <div className="row">
            <div className="col-1-3" key={item.UID}>{item.Email}  </div>
            <div className="col-1-3">{item.Name}</div>
            <div className="col-1-3">{item.GroupID}</div>
          </div>
           ))} </div> );}
           else{
           return <div><div className="row">Search Users: <input type="text" maxLength="45" onInput={this.onUserSearch}/></div></div>;
           }
        }
      }
    }

  ReactDOM.render(
  <UsersSearch/>, document.getElementById('rootUsers'));