var vue = new Vue({
    el: "#showBox",
    data: {
        serviceList: [],
        clickServiceName: "",
        blockRequestCount:0,
        methodList: [],
        clientList: [],
        serverList: []
    },
    methods: {

    }
})

var server = new Vue({
    el: "#serverNav",
    data: {
        applicationInfo: {}
    },
    methods: {}
})


// TODO 页面切换控制方法
function loadHtml(category, args) {
    showTable(category)
    switch (category) {
        case "serviceList":
            getServiceList()
            break;
        case "methodList":
            getMethodList(args)
            break;
        case "clientList":
            getClientList();
            break;
        case "serverList":
            getServerList()
            break;
    }
}


// TODO 0 本机基本信息
function applicationInfo() {
    getListData("application-info", new Object(), function (res) {
        server.applicationInfo = res;
        $("#web-title").html(res.applicationName)
    })
}


// TODO 1 本机提供的服务列表
function getServiceList() {
    getListData("serviceList", new Object(), function (array) {
        console.log(array)
        vue.serviceList = array;
    })
}


// TODO 2 指定服务提供的方法
function getMethodList(td) {
    var tr = $(td).parent().parent();
    var array = $(tr).children();
    var serviceName = array[1].innerHTML;
    var object = new Object();
    object.serviceName = serviceName;
    getListData("methodList", object, function (res) {
        vue.clickServiceName = res.serviceName
        vue.methodList = res.list;
    })
}


// TODO 3 当前服务已连接的客户端
function getClientList() {
    getListData("clientList", new Object(), function (array) {
        vue.clientList = array
    })
}


// TODO 4 当前客户端已连接的服务端
function getServerList() {
    getListData("serverList", new Object(), function (res) {
        vue.serverList = res.list;
        vue.blockRequestCount = res.blockRequestCount;
        console.log(res.blockRequestCount)
    })
}






// TODO 5 手动连接到服务器
function manualConnect(button) {
    var tr = $(button).parent().parent();
    var array = $(tr).children();
    var serverHost = array[0].innerHTML;
    var serverPort = array[1].innerHTML;
    var serviceName = array[2].innerHTML;
    var object = new Object();
    object.name = serviceName;
    object.host = serverHost;
    object.port = serverPort;
    getListData("connect-server", object, function (message) {
        layerMsg(message, 2000)
        if (message == "连接成功") {
            loadHtml("serverList")
        }
    })
}


// layer
function layerMsg(content, time) {
    if (time == null || time == undefined) {
        time = 1000;
    }
    layer.msg(content, {
        time: time //自动关闭
    });
}


function showTable(category) {
    $("div.listClass").hide()
    var showId = "#" + category + "Div";
    $(showId).show();
}


function getListData(category, params, callback) {
    var url = getUrl(category);
    postJSON(url, params, callback);
}


function getUrl(category) {
    var url = getHostAndPort() + "/poseidon-web-manage/" + category

    function getHostAndPort() {
        return window.location.href.split("marsatg.html")[0];
    }

    return url;
}


function postJSON(URL, data, callback) {

    $.ajax({
        url: URL,
        dataType: "application/json",            //返回格式为json
        contentType: "application/json",
        async: true,                 //请求是否异步，默认为异步，这也是ajax重要特性
        data: JSON.stringify(data),                  //参数值
        type: "POST",                //请求方式
        beforeSend: function () {
            //请求前的处理

        },
        success: function (res) {
            //请求成功时处理
            if (callback) {
                var res = err.responseText;
                try {
                    var json = eval("(" + res + ")");
                    callback(json)
                }catch (error){
                    callback(res)
                }
            }
        },
        complete: function () {
            //请求完成的处理
        },
        error: function (err) {
            //请求出错处理
            if (callback) {
                var res = err.responseText;
                try {
                    var json = eval("(" + res + ")");
                    callback(json)
                }catch (error){
                    callback(res)
                }
            }
        }

    })
}