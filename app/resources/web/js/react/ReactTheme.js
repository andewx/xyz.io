class ReactTheme extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            palette: [[20,20,20],[40,40,40],[40,40,40],[80,80,80],[140,140,140],[220,220,220]],
            files: [],
            name: "",
            loaded: false
        }

        this.colors = this.colors.bind(this)
        this.submit = this.submit.bind(this)
    }

    componentDidMount(){
        this.setState({loaded:true});
    }

    colors(){ //fetch colormind.io CORS request
        event.preventDefault();
        var url = "http://colormind.io/api/";
        var data = {
            model : "ui"
        }

        var http = new XMLHttpRequest();
         $("#progressBar").toggle();
        http.onreadystatechange = function() {
            if(http.readyState == 4 && http.status == 200) {
                var myPal = JSON.parse(http.responseText).result;
                this.setState({palette:myPal})
                $("#progressBar").toggle();
            }else{

                console.log("Error CORS Request (http://colormind.io/api/")

                return myPal
            }
        }.bind(this)

        http.open("POST", url, true);
        http.send(JSON.stringify(data));

    }

    submit(){
        var formData = new FormData($('form')[0]);
        var name = $("#name").val();
        var files = $("#files").val();
        formData.append("name", name);
        formData.append("palette", JSON.stringify(this.state.palette))
        var url = '/theme/create';
        $.ajax(url, {
            type: 'POST',
            data: formData,
            cache: false,
            processData: false,
            contentType: false
        }).done(function(){
            window.location.replace("http://localhost:8080/compl");
        });
    }


    render(){
        const {palette, files, name, loaded} = this.state;

       var convertRgbCss = function(rgbVal){
            return "rgb("+ rgbVal[0] +"," + rgbVal[1] + "," + rgbVal[2]+ ")"
        }

        if(loaded){
            return (
                <div className="section">
                                    <div className="dash-element-1 align-center">
                                    <div id="progressBar" className="section is-hidden"><progress>...loading</progress></div>
                                        <div className="dash-item-large" id="p1" style={{background : convertRgbCss(palette[0])}}><div className="lighten-overlay"><div className="dash-item-caption">1</div></div></div>
                                        <div className="dash-item-large" id="p2" style={{background : convertRgbCss(palette[1])}}><div className="lighten-overlay"><div className="dash-item-caption">2</div></div></div>
                                        <div className="dash-item-large" id="p3" style={{background : convertRgbCss(palette[2])}}><div className="lighten-overlay"><div className="dash-item-caption">3</div></div></div>
                                        <div className="dash-item-large" id="p4" style={{background : convertRgbCss(palette[3])}}><div className="lighten-overlay"><div className="dash-item-caption">4</div></div></div>
                                        <div className="dash-item-large" id="p5" style={{background : convertRgbCss(palette[4])}}><div className="lighten-overlay"><div className="dash-item-caption">5</div></div></div>
                    </div>
                    <div className="section"><div className="dash-button" style={{padding: "20px", marginLeft:"150px"}} onClick={this.colors}><div className="fa fa-refresh" style={{margin:"0px 10px 0px 0px"}}></div>Generate</div></div>
                <div className="dash-section">
                    <div className="dash-element-2">
                        <div className="dash-item"><h3>Theme Name</h3> <input type="text" id="name" name="name" defaultValue="Theme Name" maxLength="45" required pattern="[a-zA-Z0-9]+"/></div>
                    </div>
                    <div className="dash-element-2"> <div className="dash-item"><h3>Theme Files</h3><form encType="multipart/form-data"><input type="file" name="files"  multiple/></form></div></div>

                 <div className="dash-element-2">

                       <div className="dash-item"><h3>Submit</h3><p className="caption">When you create a theme its own file directory is created under /themes/:name. Any files
                       you add will either be rooted here (html), or exist in corresponding /js, /img, /css directories depending on the source type.  IMPORTANT! For now theme files should be added as index.html files</p>
                       <br></br>
                       <p className="caption">There is no requirement to add html files first. You can edit this theme later and add all the files required for your project</p></div>
<br></br>
<div className="dash-item"><div className="dash-button" onClick={this.submit}><div className="fa fa-save" style={{margin:"0px 10px 0px 0px"}}></div>Submit</div></div>
                 </div>

                </div>
                </div>
            );
        }

        return (<div className="section"><progress>...loading</progress></div>);
    }

}

ReactDOM.render(<ReactTheme/>, document.getElementById("rootTheme"));