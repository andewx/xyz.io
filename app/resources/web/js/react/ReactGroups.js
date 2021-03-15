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

   handleSave(index, event){
    event.preventDefault();
    const groupObj = this.state.groups[index];
    Pace.restart();
    $.post("/model/update/Group/" + groupObj.UID, groupObj)
    }

      handleDelete(event){
      event.preventDefault();
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



  render() {

    const { error, isLoaded, groups, searchName } = this.state;
    const listItems = groups.map((group, index) =>
     <li key={group.UID}><div className="item">
        <div className="itemName">{group.Name}</div>
        <div className="itemIcons pull-right"><a href="#" onClick={this.handleDelete} className="fa-minus fa"></a><a href="#" uid={group.UID} className="fa-edit fa" onClick={this.handleEdit}></a></div>
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