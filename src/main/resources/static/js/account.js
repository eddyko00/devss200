
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

        var htmlAdmin = '<button id="splunkbtn"  >Splunk Analyize</button>';
        htmlAdmin += '<button id="monitorbtn"  >RealTime Monitor</button>';
        htmlAdmin += '<button id="regressionbtn"  >QA Regression Testing</button>';
        $("#adminid").html(htmlAdmin);
        if (custObj.type == 99) {
            var htmlAdmin = '<button id="lockbtn" >Lock</button>';
            htmlAdmin += '<button id="serverbtn"  >Server</button>';
            $("#adminid").append(htmlAdmin);
        }

        $("#accheader").html("User Account");


        $("#splunkbtn").click(function () {
            var iisWebObj = {'custObjStr': custObjStr, 'servObjListStr': servObjListStr};
            window.localStorage.setItem(iisWebSession, JSON.stringify(iisWebObj));
            window.location.href = "splunkanalyize.html";
            return;
        });

        $("#lockbtn").click(function () {

            $.ajax({
                url: iisurl + "cust/" + custObj.username + "/sys/lock",

                crossDomain: true,
                cache: false,
                success: function (resultLockObjList) {
                    console.log(resultLockObjList);
                    if (resultLockObjList == null) {
                        window.location.href = "#page-lock";
                    }
                    $("#lockid").html(' ');
                    for (i = 0; i < resultLockObjList.length; i++) {
                        var lockObj = resultLockObjList[i];
                        var trStr = lockObj.lockdatedisplay + '  ' + lockObj.lockname +
                                '  type:' + lockObj.type + '<br>' + lockObj.comment;
                        var htmlName = '<h3>' + trStr + '</h3>';
                        $("#lockid").append('<li >' + htmlName + '</li>');
                    }
                    window.location.href = "#page-lock";
                    return;
                }
            });
        });

        $("#lock1btn").click(function () {

            $.ajax({
                url: iisurl + "cust/" + custObj.username + "/sys/lock",

                crossDomain: true,
                cache: false,
                success: function (resultLockObjList) {
                    console.log(resultLockObjList);
                    if (resultLockObjList == null) {
                        window.location.href = "#page-lock";
                    }
                    $("#lockid").html(' ');
                    for (i = 0; i < resultLockObjList.length; i++) {
                        var lockObj = resultLockObjList[i];
                        var trStr = lockObj.lockdatedisplay + '  ' + lockObj.lockname +
                                '  type:' + lockObj.type + '<br>' + lockObj.comment;
                        var htmlName = '<h3>' + trStr + '</h3>';
                        $("#lockid").append('<li >' + htmlName + '</li>');
                    }
                    window.location.href = "#page-lock";
                    return;
                }
            });
        });


        $("#serverbtn").click(function () {

            $.ajax({
                url: iisurl + "server",

                crossDomain: true,
                cache: false,
                success: function (resultServerList) {
                    console.log(resultServerList);
                    if (resultServerList == null) {
                        window.location.href = "#page-lock";
                    }
                    $("#serverid").html(" ");
                    for (i = 0; i < resultServerList.length; i++) {
                        var srvObj = resultServerList[i];
                        var trStr = srvObj.lastServUpdateESTdate + '   ' + srvObj.serverName;
                        trStr += '<br>Maintance:' + srvObj.sysMaintenance
                        trStr += '<br>processTimerCnt:' + srvObj.processTimerCnt + '   autoNNCnt:' + srvObj.autoNNCnt;
                        trStr += '<br>Total Stock=' + srvObj.totalStock + '   Total StockAcc:' + srvObj.totalStockAcc;

                        trStr += '<br>' + srvObj.timerMsg;
                        trStr += '<br>RESTreq:' + srvObj.cntRESTrequest + '   Ex:' + srvObj.cntRESTexception;
                        trStr += '<br>InterReq:' + srvObj.cntInterRequest + '   Ex:' + srvObj.cntInterException;
                        var htmlName = ' ' + trStr + ' ';
                        $("#serverid").append('<li ></li>' + htmlName);
                    }
                    window.location.href = "#page-server";
                    return;
                }
            });
        });


    },
};
app.initialize();





