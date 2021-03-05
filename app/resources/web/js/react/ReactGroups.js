class GroupsComponent extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      error: null,
      isLoaded: false,
      groups: []
    };

  }

  ajax(){
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
    this.ajax();
  }


  render() {

    const { error, isLoaded, groups, searchName } = this.state;
    const listItems = groups.map((group) =>
    <li key={group.UID}>{group.Name}</li>
    );
    if (error) {
      return <div className="row"><h3>Groups</h3><a className="fa-plus fa pull-right"></a></div>
    } else if (!isLoaded) {
      return <div className="row"><h3>Groups</h3><a className="fa-plus fa pull-right"></a></div>
    } else {
         return (
        <div>
        <div className="row"><h3>Groups</h3><a className="fa-plus fa pull-right"></a></div>
            <ul>{listItems}</ul>
         </div>
         );
       }
    }
 }

   ReactDOM.render(
   <GroupsComponent/>, document.getElementById('rootGroups')
   );