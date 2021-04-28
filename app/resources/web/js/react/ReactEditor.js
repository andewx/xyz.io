"use strict";

function Pages() {
  const [request, setRequest] = React.useState({
    items: [],
    loaded: false
  });
  const url = "/site/getPages";
  React.useEffect(() => {
    let mounted = true;
    let name = $("#siteName").html();

    async function load() {
      const res = await fetch(url + "/" + name);
      const result = await res.json();

      if (mounted) {
        setRequest({
          items: result,
          loaded: true
        });
      }
    }

    load();
    return function cleanup() {
      mounted = false;
    };
  }, []);

  function renderPages(load, list) {
    if (load) {
      return /*#__PURE__*/ React.createElement(
        "ul",
        null,
        list.map((item, index) =>
          /*#__PURE__*/ React.createElement(Page, {
            key: item,
            name: item.Name,
            index: index
          })
        )
      );
    } else {
      return /*#__PURE__*/ React.createElement("ul", null);
    }
  }

  return renderPages(request.loaded, request.items);
}

function Page(props) {
  const [name, setName] = React.useState(props.name);
  const [index, setIndex] = React.useState(props.index);

  function render() {
    let siteURL = "http://localhost:8080/site/editSite/" + $("#siteName").html() + "/" + name;
    return /*#__PURE__*/ React.createElement(
      "li",
      null,
      /*#__PURE__*/ React.createElement(
        "a",
        {
          href: siteURL
        },
        name
      )
    );
  }

  return render();
}

ReactDOM.render(
  /*#__PURE__*/ React.createElement(Pages, null),
  document.getElementById("cmsPages")
);
