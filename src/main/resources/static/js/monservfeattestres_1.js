
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
        var serv = iisWebObj.serv;
        var featIDObjListStr = iisWebObj.featIDObjListStr;
        var featIDObjList = JSON.parse(featIDObjListStr);
        var featObjId = iisWebObj.featObjId;
        var cmd = iisWebObj.cmd;
        var iisurllocal = iisurl_LOCAL;
        $.ajax({
            url: iisurllocal + "/cust/" + custObj.username + "/id/" + custObj.id + "/serv/" + serv
                    + "/id/" + featObjId + "/rt/" + cmd,
            crossDomain: true,
            cache: false,
            beforeSend: function () {
                $("#loader").show();
            },

            error: function () {
                alert('network failure');
                window.location.href = "index.html";
            },

            success: function (resultList) {
//                    console.log(resultListStr);

                var resultListStr = JSON.stringify(resultList, null, '\t');
                var iisWebObj = {'custObjStr': custObjStr, 'servObjListStr': servObjListStr, 'serv': serv,
                    'featIDObjListStr': featIDObjListStr, 'featObjId': featObjId, 'cmd': cmd, 'resultListStr': resultListStr};
                window.localStorage.setItem(iisWebSession, JSON.stringify(iisWebObj));
                window.location.href = "monservfeattestres.html";
            }
        });

    }
};
app.initialize();



