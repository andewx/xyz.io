//Fetch Page JSON
let site = $("#siteName").html()
let page = $("#sitePage").html()
let editor = {}

const editorInit = {
           autofocus: true,
           tools: {
               image:{
                class: ImageTool,
                config: {
                 endpoints: {
                   byFile: 'http://localhost:8080/site/addFiles/'+site,
                   byUrl: 'http://localhost:8080/site/fetchUrl/'+site,
                 }
                }
               },
               paragraph:{
                   class: Paragraph,
                   inlineToolbar: true,
               },
               list:{
                   class: List,
                   inlineToolbar: true,
               },
              header: {
                    class: Header,
                    config: {
                      placeholder: 'Enter a header',
                      levels: [2, 3, 4],
                      defaultLevel: 3
                    }
                  },
              raw: RawTool,
           },
           holder: "page-root",
           data: {}
 }

  const pageJsonData = (async function(){
      const pageData = await fetch("/site/getPageData/"+site+ "/"+page);
      const pageJSON = await pageData.json();
      return pageJSON;
    })()

 pageJsonData.then(function(value){
    editorInit.data = value;
    editor = new EditorJS(editorInit)
 });





const saveButton = document.getElementById('save-button');
const viewButton = document.getElementById('view-button');

    saveButton.addEventListener('click', () => {
      editor.save().then( savedData => {
         let site = $("#siteName").html()
         let page = $("#sitePage").html()
         const parser = new edjsParser();
         let jsonReq = {html : parser.parse(savedData), pageJson: JSON.stringify(savedData)}

         $.ajax('/site/savePage/'+site+'/'+page, {
            method: 'POST',
            data: jsonReq
         });
      })
    })

    viewButton.addEventListener('click', ()=>{
        let url = "http://localhost:8080/site/r/"+ $("#siteName").html() + "/" + $("#sitePage").html();
        window.open(url)
    })

