
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

        var featRtObj = null;

        var featObj0 = featIDObjList[0];
        $("#accheader").html("Mon (" + serv + ") Feature");

        $("#myid").html(featObj0.name); //clear the field
      
        for (i = 0; i < featIDObjList.length; i += 2) {
            var featObj = featIDObjList[i];
//    private String cusid="";
//    private String banid="";
//    private String tiid=""; 
            var custid = featObj.cusid;
            var banid = featObj.banid;
            var tiid = featObj.tiid;

            var objId = i + 10;
            var htmlName = '<div class="ui-grid-b">';
            htmlName += '<div class="ui-block-a" >Cust: ' + custid + '</div>';
            htmlName += '<div class="ui-block-b">Ban: ' + banid + '</div>';
            htmlName += '<div class="ui-block-b">Tid ' + tiid + '</div>';
            htmlName += '</div>';
            htmlName += '<br>Detail splunk trace:<br>uuid: ' + featObj.uid;


//            htmlName += '<br>' + featObj.name + "<br> execTime: " + featObj.exec;
//            htmlName += '<br>' + featObj.uid;
            var prodDataStr = featObj.data;
            var prodData = JSON.parse(prodDataStr);
            var result = prodData.postParam.split('^').join('"');
            result = result.split('\\').join('');
            htmlName += '<br>payload:<br>' + result;
            htmlName += '<br>Detail SOA donwstream:';
            var flowList = prodData.flow;
            for (j = 0; j < flowList.length; j++) {
                var flow = flowList[j];
                flow = flow.split('^').join('"');
                flow = flow.split('\\').join('');
                htmlName += '<br>' + j + " " + flow;
            }
//            $("#myid").append('<li id="' + objId + '">' + htmlName + '</li>');

            $("#myid").append('<li id="' + objId + '"><a href="#">' + htmlName + '</a></li>');
        }

        $("ul[id*=myid] li").click(function () {
            var accId = $(this).attr('id');
            console.log(accId);
            if (accId == 0) {
//                alert(accId);
                return;
            }

            var featObj = featIDObjList[accId - 10];
            featRtObj = featObj;
            var prodDataStr = featObj.data;
            var prodData = JSON.parse(prodDataStr);
            var cmdList = prodData.cmd;
            var htmlName = "";
            $("#detailid").html(" ");

            var htmlName = '';
            $("#detailid").append(htmlName);

            htmlName = "<br>Raw Data:";
            var featObjStr = JSON.stringify(featObj, null, '\t');
            var result = featObjStr.split('^').join('"');
            result = result.split('\\').join('');
            htmlName += result;
            $("#detailid").append('<li ></li>' + htmlName);

            window.location.href = "#page-detail";
        });

        $("#rtbtn").click(function () {
            var featObjId = featRtObj.id;
        
            var iisWebObj = {'custObjStr': custObjStr, 'servObjListStr': servObjListStr, 'serv': serv,
                'featIDObjListStr': featIDObjListStr, 'featObjId': featObjId};

            window.localStorage.setItem(iisWebSession, JSON.stringify(iisWebObj));
            window.location.href = "monservfeattest.html";
            return;
        });


    }
};
app.initialize();



