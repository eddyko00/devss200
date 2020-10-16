
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
        var resultMonObjListStr = iisWebObj.resultMonObjListStr;
        var resultMonObjList = JSON.parse(resultMonObjListStr);
        var serv = iisWebObj.serv;
        var url = iisWebObj.url;
        var monCmd = iisWebObj.monCmd;
        
        if (monCmd === 'start') {
            alert ('start regression using url='+url);
        }
        
        $.ajax({
            url: iisurl + "/cust/" + custObj.username + "/id/" + custObj.id
                    + "/regression/" + monCmd + "?app=" + serv + "&url=" + url,
            crossDomain: true,
            cache: false,
            beforeSend: function () {
                $("#loader").show();
            },

            error: function () {
                alert('network failure');
                window.location.href = "index.html";
            },

            success: function (resultObjList) {
//                console.log(resultMonObjList);
                if (resultObjList == 1) {
                    alert("Return Status - Successful");

                } else if (resultObjList == 2) {
                    alert("Return Status - Already started");
                } else {
                    alert("Return Status " + resultObjList);
                }
                if (resultObjList == null) {
                    window.location.href = "index.html";
                }

                var iisWebObj = {'custObjStr': custObjStr, 'servObjListStr': servObjListStr, 
                    'serv': serv, 'resultMonObjListStr': resultMonObjListStr};
                window.localStorage.setItem(iisWebSession, JSON.stringify(iisWebObj));
                window.location.href = "regmonitor_1.html";

            }
        });

    }
};
app.initialize();



