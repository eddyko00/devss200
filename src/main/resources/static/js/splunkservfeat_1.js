
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
        var feat = iisWebObj.feat;
        $.ajax({
            url: iisurl + "/cust/" + custObj.username + "/id/" + custObj.id + "/serv/" + serv 
                    + "/feature/name?name="+feat,
            crossDomain: true,
            cache: false,
            beforeSend: function () {
                $("#loader").show();
            },

            error: function () {
                alert('network failure');
                window.location.href = "index.html";
            },

            success: function (resultFeatIDObjList) {
                console.log(resultFeatIDObjList);

                var featIDObjListStr = JSON.stringify(resultFeatIDObjList, null, '\t');
                var iisWebObj = {'custObjStr': custObjStr, 'servObjListStr': servObjListStr, 'serv': serv, 'featIDObjListStr': featIDObjListStr};
                window.localStorage.setItem(iisWebSession, JSON.stringify(iisWebObj));
                window.location.href = "splunkservfeat.html";

            }
        });

    }
};
app.initialize();



