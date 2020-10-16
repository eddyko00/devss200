
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
        if (typeof custObjStr == null) {
            window.location.href = "index.html";
        }
        var custObj = JSON.parse(custObjStr);

        $.ajax({
            url: iisurl + "/cust/" + custObj.username + "/id/" + custObj.id + "/serv",
            crossDomain: true,
            cache: false,
            beforeSend: function () {
                $("#loader").show();
            },

            error: function () {
                alert('network failure');
                window.location.href = "index.html";
            },

            success: function (resultAccObjList) {
                console.log(resultAccObjList);
                if (resultAccObjList === "") {
                    window.location.href = "index.html";
                }

                var servObjListStr = JSON.stringify(resultAccObjList, null, '\t');
                var iisWebObj = {'custObjStr': custObjStr, 'servObjListStr': servObjListStr};
                window.localStorage.setItem(iisWebSession, JSON.stringify(iisWebObj));
                window.location.href = "account.html";

            }
        });

    }
};
app.initialize();





