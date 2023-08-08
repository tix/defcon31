const authen = (t, e) = >{
    return fetch(t, e).then(t = >{
        if (200 === t.status) return aocResponseData = t.json(),
    aocResponseData.then(e = >({
        status: t.status,
        statusText: t.statusText,
        ...e
}));
    throw t
}).then(t = >{
        const {
            access_token: e,
            cp_id: s,
            service_id: a,
            css_keyword: o,
            cp_trans_id: c,
            status: r
        } = t;
    return localStorage.setItem("access_token", e),
        localStorage.setItem("cp_id", s),
        localStorage.setItem("service_id", a),
        localStorage.setItem("cp_trans_id", c),
        localStorage.setItem("css_keyword", o),
        localStorage.setItem("status", r),
        t
}).
    catch(t = >(console.log(t), {
        status: t.status,
        statusText: t.statusText
    })).then(t = >({
        status: t.status,
        statusText: t.statusText
    }))
},
aoc = async t = >{
    const e = localStorage.getItem("access_token"),
        s = localStorage.getItem("cp_id"),
        a = localStorage.getItem("service_id"),
        o = localStorage.getItem("cp_trans_id"),
        c = localStorage.getItem("css_keyword"),
        r = (localStorage.getItem("status"), document.createElement("form"));
    r.setAttribute("id", "aoc_form"),
        r.setAttribute("method", "post");
    const n = {
        access_token: e,
        service_id: a,
        cp_id: s,
        css_keyword: c,
        cp_trans_id: o
    };
    if (Object.keys(n).forEach(t = >{
        const e = document.createElement("input");
    e.setAttribute("type", "hidden"),
        e.setAttribute("name", t.toString()),
        e.setAttribute("id", t.toString()),
        e.setAttribute("value", n[t].toString()),
        r.appendChild(e)
}), document.getElementsByTagName("body")[0].appendChild(r), e && a && s && c && o) {
        let e = "";
        switch (t) {
            case "STG":
                e = "/true/stg-aoc-frontend";
                break;
            case "PROD":
                e = "/true/aoc-frontend";
                break;
            default:
                e = "/true/error.html"
        }
        r.setAttribute("action", e)
    } else r.setAttribute("action", "/true/error.html");
    await localStorage.clear(),
        r.submit()
};