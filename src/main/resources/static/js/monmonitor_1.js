
var app = {

// Application Constructor
    initialize: function () {

        $(document).ready(function () {

        });

        var iisWebSession = "iisWebSession";
        iisurl = iisurl.replace("abc", "");
        iisurl = iisurl.replace("abc", "");


        var iisWebObjStr = window.localStorage.getItem(iisWebSession);
        var iisWebObj = JSON.parse(iisWebObjStr);
        console.log(iisWebObj);

        var custObjStr = iisWebObj.custObjStr;
        if (custObjStr == null) {
            window.location.href = "index.html";
        }
        var custObj = JSON.parse(custObjStr);
        var servObjListStr = iisWebObj.servObjListStr;
        var servObjList = JSON.parse(servObjListStr);

        $.ajax({
            url: iisurl + "/cust/" + custObj.username + "/id/" + custObj.id + "/mon",
            crossDomain: true,
            cache: false,
            beforeSend: function () {
                $("#loader").show();
            },

            error: function () {
                alert('network failure');
                window.location.href = "index.html";
            },

            success: function (resultMonObjList) {
//                console.log(resultMonObjList);
                if (resultMonObjList == null) {
                    window.location.href = "index.html";
                }

                var resultMonObjListStr = JSON.stringify(resultMonObjList, null, '\t');
                var iisWebObj = {'custObjStr': custObjStr, 'servObjListStr': servObjListStr, 'resultMonObjListStr': resultMonObjListStr};
                window.localStorage.setItem(iisWebSession, JSON.stringify(iisWebObj));
                window.location.href = "monmonitor.html";

            }
        });

    }
};
app.initialize();



