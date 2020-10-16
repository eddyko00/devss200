
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
        var repId = iisWebObj.repId;
        var reportTCList = null;

        var monObj = resultMonObjList[repId];
        $("#myid").html('');
        var prodDataStr = monObj.data;
        if (typeof prodDataStr !== 'undefined') {
            if (prodDataStr !== "") {
                var prodData = JSON.parse(prodDataStr);
                if (monObj.uid === 'user') {
                    var repList = prodData.featList;
                    for (j = 0; j < repList.length; j++) {
                        var report = repList[j];
                        $("#myid").append('<li>' + report + '</li>');
                    }
                } else {
                    var repList = prodData.reportList;
                    reportTCList = repList;
                    for (j = 0; j < repList.length; j++) {
                        var objId = j + 10;
                        var report = repList[j];
                        var res = report.split(",");
                        if (isNaN(res[0])) {
                            var report = repList[j];
                            $("#myid").append('<li>' + report + '</li>');
                            continue;
                        }

                        if (res.length <= 5) {
                            var report = repList[j];
                            $("#myid").append('<li>' + report + '</li>');
                            continue;
                        }
                        var namep = res[4].split(":");
                        var pas = namep[namep.length - 1];
                        var htmlSt = '<div class="ui-grid-b">';
                        htmlSt += '<div class="ui-block-a" style="width:10%">' + pas + '</div>';
                        var exec = res[5];
                        exec = exec / 1000;
                        var execSt = exec.toFixed(2) + ' sec';
                        htmlSt += '<div class="ui-block-b" style="width:10%">' + execSt + '</div>';
                        htmlSt += '<div class="ui-block-c">' + res[4] + '</div>';
                        htmlSt += '</div>';
                        var htmlName = '<li id="' + objId + '"><a href="#">' + htmlSt;
                        htmlName += '</a></li>';
                        $("#myid").append(htmlName);
                    }
                }
            }
        }



        $("ul[id*=myid] li").click(function () {
//            alert($(this).html()); // gets innerHTML of clicked li
//            alert($(this).text()); // gets text contents of clicked li
            var objId = $(this).attr('id');
            console.log(objId);
            if (objId == 0) {
                return;
            }
            if (reportTCList == null) {
                return;
            }
            var report = reportTCList[objId - 10];
            var res = report.split(",");
            if (isNaN(res[0])) {
                return;
            }

            var pid = res[0];
            $.ajax({
                url: iisurl + "/cust/" + custObj.username + "/id/" + custObj.id + "/mon/pid/" + pid,
                crossDomain: true,
                cache: false,
                beforeSend: function () {
                    $("#loader").show();
                },

                error: function () {
                    alert('network failure');
                    window.location.href = "index.html";
                },

                success: function (resultObj) {
//                console.log(resultMonObjList);
                    if (resultObj == null) {
                        window.location.href = "index.html";
                    }

                    $("#detailid").html('');
                    var prodDataStr = resultObj.data;
                    var prodData = JSON.parse(prodDataStr);
                    var repList = prodData.flow;

                    for (j = 0; j < repList.length; j++) {
                        var report = repList[j];
                        report = report.split('^').join('"');
                        $("#detailid").append('<li>' + report + '</li>');
                    }
                }
            });

            window.location.href = "#page-detail";

        });


    }
};
app.initialize();



