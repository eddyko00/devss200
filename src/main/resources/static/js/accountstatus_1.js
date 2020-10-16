
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
        var servObjListStr = iisWebObj.servObjListStr;
        var servObjList = JSON.parse(servObjListStr);

        $.ajax({
            url: iisurl + "cust/" + custObj.username + "/sys/lock",

            crossDomain: true,
            cache: false,
            beforeSend: function () {
                $("#loader").show();
            },

            error: function () {
                alert('network failure');
                window.location.href = "index.html";
            },
            success: function (lockObjList) {
                console.log(lockObjList);
                var lockObjListStr = JSON.stringify(lockObjList, null, '\t');
                $.ajax({
                    url: iisurl + "server",

                    crossDomain: true,
                    cache: false,
                    success: function (serverList) {
                        var serverListStr = JSON.stringify(serverList, null, '\t');
                        var iisWebObj = {'custObjStr': custObjStr, 'servObjListStr': servObjListStr,
                            'lockObjListStr': lockObjListStr, 'serverListStr': serverListStr};
                        window.localStorage.setItem(iisWebSession, JSON.stringify(iisWebObj));
                        window.location.href = "accountstatus.html";
                        return;
                    }
                });

                return;
            }
        });

    }
};
app.initialize();





