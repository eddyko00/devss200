
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

        var htmlAdmin = '<button id="splunkbtn"  >Splunk Analyize</button>';
        htmlAdmin += '<button id="monitorbtn"  >RealTime Monitor</button>';
        htmlAdmin += '<button id="regressionbtn"  >QA Regression Testing</button>';
        $("#adminid").html(htmlAdmin);
        if (custObj.type == 99) {
            var htmlAdmin = '<br><br><button id="sysbtn" >System Status</button>';
            $("#adminid").append(htmlAdmin);
        }

        $("#accheader").html("User Account");


        $("#monitorbtn").click(function () {
            var iisWebObj = {'custObjStr': custObjStr, 'servObjListStr': servObjListStr};
            window.localStorage.setItem(iisWebSession, JSON.stringify(iisWebObj));
            window.location.href = "monanalyize.html";
            return;
        });

        $("#splunkbtn").click(function () {
            var iisWebObj = {'custObjStr': custObjStr, 'servObjListStr': servObjListStr};
            window.localStorage.setItem(iisWebSession, JSON.stringify(iisWebObj));
            window.location.href = "splunkanalyize.html";
            return;
        });

        $("#regressionbtn").click(function () {
            var iisWebObj = {'custObjStr': custObjStr, 'servObjListStr': servObjListStr};
            window.localStorage.setItem(iisWebSession, JSON.stringify(iisWebObj));
            window.location.href = "reganalyize.html";
            return;
        });

        $("#sysbtn").click(function () {
            var iisWebObj = {'custObjStr': custObjStr, 'servObjListStr': servObjListStr};
            window.localStorage.setItem(iisWebSession, JSON.stringify(iisWebObj));
            window.location.href = "accountstatus_1.html";
            return;
        });

    }
};
app.initialize();





