
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
        var resultMonObjListStr = iisWebObj.resultMonObjListStr;
        var resultMonObjList = JSON.parse(resultMonObjListStr);
        $("#myid").html("<h4>Monitor all services </h4>"); //clear the field        
        if (resultMonObjList != null) {
            for (i = 0; i < resultMonObjList.length; i++) {
                var monObj = resultMonObjList[i];
                var objId = i + 10;
                var htmlSt = '<div class="ui-grid-b">';
                htmlSt += '<div class="ui-block-a" style="width:10%" >' + monObj.uid + '</div>';
                var statusSt = 'started';
                if (monObj.status == 5) {
                    statusSt = 'completed';
                }
                htmlSt += '<div class="ui-block-b" style="width:15%" >' + statusSt + '</div>';
                htmlSt += '<div class="ui-block-b">' + monObj.ret + '</div>';
                htmlSt += '</div>';
                var htmlName = '<li id="' + objId + '"><a href="#">' + htmlSt;
                htmlName += '</a></li>';
                $("#myid").append(htmlName);
                if (monObj.uid === 'user') {
                    var prodDataStr = monObj.data;
                    var prodData = JSON.parse(prodDataStr);
                    var repList = prodData.reportList;
                    if (repList == null) {
                        var htmlName = '<li id="' + objId + '"></li>';
                        $("#myid").append(htmlName);
                        continue;
                    }
                    for (j = 0; j < repList.length; j++) {
                        var report = repList[j];
                        var res = report.split(",");
                        if (res.length < 5) {
//                            $("#myid").append(report);
                            continue;
                        }
                        var htmlSt = '<div class="ui-grid-c">';
                        htmlSt += '<div class="ui-block-a">' + res[0] + '</div>';
                        htmlSt += '<div class="ui-block-b">' + res[2] + ' ' + res[3] + '</div>';
                        htmlSt += '<div class="ui-block-c">' + res[4] + ' ' + res[5] + '</div>';
                        var exec = res[7];
                        exec = exec / 1;
                        var execSt = exec.toFixed(2) + ' sec';
                        htmlSt += '<div class="ui-block-d">' + execSt + '</div>';
                        htmlSt += '</div>';
                        var htmlName = '<li id="' + objId + '">' + htmlSt;
                        htmlName += '</li>';
                        $("#myid").append(htmlName);

                    }
                }
            }
        } else {
            $("#myid").html("No report running ");
        }



        $("ul[id*=myid] li").click(function () {
//            if (custObj.username.toUpperCase() === "GUEST") {
//                alert("Please register a Dev user to access this operation");
//                return;
//            }
            var objId = $(this).attr('id');
            console.log(objId);
            if (objId == 0) {
                return;
            }
            var monObj = resultMonObjList[objId - 10];
            $("#detailid").html('');
            var prodDataStr = monObj.data;
            if (typeof prodDataStr !== 'undefined') {
                if (prodDataStr !== "") {
                    var prodData = JSON.parse(prodDataStr);
                    if (monObj.uid === 'user') {
                        var repList = prodData.featList;
                        for (j = 0; j < repList.length; j++) {
                            var report = repList[j];
                            $("#detailid").append('<li>' + report + '</li>');
                        }
                    } else {
                        var repId = objId - 10;
                        var iisWebObj = {'custObjStr': custObjStr, 'servObjListStr': servObjListStr, 'resultMonObjListStr': resultMonObjListStr, 'repId': repId};
                        window.localStorage.setItem(iisWebSession, JSON.stringify(iisWebObj));
                        window.location.href = "monmonitortc.html";
                        return;
                    }
                }
            }
            window.location.href = "#page-detail";

        });

        $("#stopbtn").click(function () {
            if (custObj.type != 99) {
                alert("Only Admin user supprots this operation");
                return;
            }
            var monCmd = 'stop';
            var serv = "";
            var iisWebObj = {'custObjStr': custObjStr, 'servObjListStr': servObjListStr, 'resultMonObjListStr': resultMonObjListStr,
                'monCmd': monCmd, 'serv': serv};
            window.localStorage.setItem(iisWebSession, JSON.stringify(iisWebObj));
            window.location.href = "monmonitor_2.html";
            return;
        });

//        $("#startbtn").click(function () {
//            if (custObj.type != 99) {
//                alert("Only Admin user supprots this operation");
//                return;
//            }            
//            var monCmd = 'start';
//            var iisWebObj = {'custObjStr': custObjStr, 'servObjListStr': servObjListStr, 'resultMonObjListStr': resultMonObjListStr, 'monCmd': monCmd};
//            window.localStorage.setItem(iisWebSession, JSON.stringify(iisWebObj));
//            window.location.href = "monmonitor_2.html";
//            return;
//        });


        $("#startbtn").click(function () {
            window.location.href = "#page_conf";
        });

        $("#savesubmit").click(function () {

            if (custObj.type != 99) {
                alert("Only Admin user supprots this operation");
                return;
            }
            var monCmd = 'start';
            var serv = $('#myidtrmodel').val();
            if (serv === "all") {
                serv = "";
            }
            var iisWebObj = {'custObjStr': custObjStr, 'servObjListStr': servObjListStr, 'resultMonObjListStr': resultMonObjListStr,
                'monCmd': monCmd, 'serv': serv};
            window.localStorage.setItem(iisWebSession, JSON.stringify(iisWebObj));
            window.location.href = "monmonitor_2.html";
            return;

        });

    }
};
app.initialize();



