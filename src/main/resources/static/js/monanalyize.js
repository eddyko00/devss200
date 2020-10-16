
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

        $("#myid").html(" "); //clear the field
        for (i = 0; i < servObjList.length; i+=2) {
            var servName = servObjList[i+1];
            var accId = i+10;

            var htmlName = '<li id="' + accId + '"><a href="#">Service: ' + servName;
              htmlName += '</a></li>';
            $("#myid").append(htmlName);
        }



        $("ul[id*=myid] li").click(function () {
//            alert($(this).html()); // gets innerHTML of clicked li
//            alert($(this).text()); // gets text contents of clicked li
            var accId = $(this).attr('id');
            console.log(accId);
            if (accId == 0) {
//                alert(accId);
                return;
            }
            var serv = servObjList[accId-10];
            var iisWebObj = {'custObjStr': custObjStr, 'servObjListStr': servObjListStr, 'serv': serv};
            window.localStorage.setItem(iisWebSession, JSON.stringify(iisWebObj));
            window.location.href = "monserv_1.html";
        });

        $("#monbtn").click(function () {
         
            var iisWebObj = {'custObjStr': custObjStr, 'servObjListStr': servObjListStr};
            window.localStorage.setItem(iisWebSession, JSON.stringify(iisWebObj));
            window.location.href = "monmonitor_1.html";
            return;
        });



    }
};
app.initialize();



