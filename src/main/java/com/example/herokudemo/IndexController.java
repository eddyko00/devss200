package com.example.herokudemo;

import com.afweb.model.*;
import com.afweb.model.ssns.ProdSummary;
import com.afweb.process.*;
import com.afweb.util.*;
import com.afweb.service.ServiceAFweb;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

//https://www.baeldung.com/spring-cors
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class IndexController {

    private static AFwebService afWebService = new AFwebService();

    @GetMapping("/")
    public String index() {
        return "Hello there! I'm running v1.1";
    }

    /////////////////////////////////////////////////////////////////////////    
    @RequestMapping(value = "/help", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList SystemHelpPage() {

        ArrayList arrayString = new ArrayList();

        arrayString.add("/server");
        arrayString.add("/server/url0");
        arrayString.add("/server/url0/set?url=stop");
        arrayString.add("/server/filepath");
        arrayString.add("/server/filepath/set?path=");
        arrayString.add("/server/restoressnsacc");
//
        arrayString.add("/cust/add?email={email}&pass={pass}&firstName={firstName}&lastName={lastName}");
        arrayString.add("/cust/login?email={email}&pass={pass}");
        arrayString.add("/cust/{username}/login&pass={pass}");

        arrayString.add("/cust/{username}/id/{id}/regression");
        arrayString.add("/cust/{username}/id/{id}/regression/start");
        arrayString.add("/cust/{username}/id/{id}/regression/stop");

        arrayString.add("/cust/{username}/id/{id}/mon");
        arrayString.add("/cust/{username}/id/{id}/mon/start?app=");
        arrayString.add("/cust/{username}/id/{id}/mon/stop");
        arrayString.add("/cust/{username}/id/{id}/mon/pid/{pid}");

        arrayString.add("/cust/{username}/id/{id}/regression/start?app=&url=");
        arrayString.add("/cust/{username}/id/{id}/regression/stop");

        arrayString.add("/cust/{username}/id/{id}/serv");

        arrayString.add("/cust/{username}/id/{id}/serv/prod?length={0 for all}");
        arrayString.add("/cust/{username}/id/{id}/serv/prod/summary?length={0 for all}");
        arrayString.add("/cust/{username}/id/{id}/serv/prod/id/{pid}");
        arrayString.add("/cust/{username}/id/{id}/serv/prod/id/{pid}/rt/getproductbyid");
        arrayString.add("/cust/{username}/id/{id}/serv/prod/id/{pid}/rt/callcontrol");
        arrayString.add("/cust/{username}/id/{id}/serv/prod/id/{pid}/rttest/getproductbyid");
        arrayString.add("/cust/{username}/id/{id}/serv/prod/featureall");
        arrayString.add("/cust/{username}/id/{id}/serv/prod/feature?name=");

        arrayString.add("/cust/{username}/id/{id}/serv/app?length={0 for all}");
        arrayString.add("/cust/{username}/id/{id}/serv/app/summary?length={0 for all}");
        arrayString.add("/cust/{username}/id/{id}/serv/app/id/{pid}");
        arrayString.add("/cust/{username}/id/{id}/serv/app/id/{pid}/rt/getappointment");
        arrayString.add("/cust/{username}/id/{id}/serv/app/id/{pid}/rt/searchtimeslot");

        arrayString.add("/cust/{username}/id/{id}/serv/app/featureall");
        arrayString.add("/cust/{username}/id/{id}/serv/app/feature?name=");
        arrayString.add("/cust/{username}/id/{id}/serv/app/feature/summary?name=");

        arrayString.add("/cust/{username}/id/{id}/serv/wifi?length={0 for all}");
        arrayString.add("/cust/{username}/id/{id}/serv/wifi/summary?length={0 for all}");
        arrayString.add("/cust/{username}/id/{id}/serv/wifi/id/{pid}");
        arrayString.add("/cust/{username}/id/{id}/serv/wifi/id/{pid}/rt/getdevices");
        arrayString.add("/cust/{username}/id/{id}/serv/wifi/id/{pid}/rt/getdeviceshdml");          
        arrayString.add("/cust/{username}/id/{id}/serv/wifi/id/{pid}/rt/getdevicestatus");
        arrayString.add("/cust/{username}/id/{id}/serv/wifi/id/{pid}/rttest/getdevicestatus");
        arrayString.add("/cust/{username}/id/{id}/serv/wifi/featureall");
        arrayString.add("/cust/{username}/id/{id}/serv/wifi/feature?name=");
        arrayString.add("/cust/{username}/id/{id}/serv/wifi/feature/summary?name=");

        arrayString.add("/cust/{username}/id/{id}/serv/ttv?length={0 for all}");
        arrayString.add("/cust/{username}/id/{id}/serv/ttv/summary?length={0 for all}");
        arrayString.add("/cust/{username}/id/{id}/serv/ttv/id/{pid}");
        arrayString.add("/cust/{username}/id/{id}/serv/ttv/id/{pid}/rt/getcustomertvsubscription");
        arrayString.add("/cust/{username}/id/{id}/serv/ttv/id/{pid}/rt/validatewithauth");
        arrayString.add("/cust/{username}/id/{id}/serv/ttv/id/{pid}/rt/quotewithauth");
        arrayString.add("/cust/{username}/id/{id}/serv/ttv/featureall");
        arrayString.add("/cust/{username}/id/{id}/serv/ttv/feature?name=");
        arrayString.add("/cust/{username}/id/{id}/serv/ttv/feature/summary?name=");

        arrayString.add("/cust/{username}/id/{id}/serv/wlnpro?length={0 for all}");
        arrayString.add("/cust/{username}/id/{id}/serv/wlnpro/summary?length={0 for all}");
        arrayString.add("/cust/{username}/id/{id}/serv/wlnpro/id/{pid}");
        arrayString.add("/cust/{username}/id/{id}/serv/wlnpro/id/{pid}/rt/downloadurl");
        arrayString.add("/cust/{username}/id/{id}/serv/wlnpro/featureall");
        arrayString.add("/cust/{username}/id/{id}/serv/wlnpro/feature?name=");
        arrayString.add("/cust/{username}/id/{id}/serv/wlnpro/feature/summary?name=");

        arrayString.add("/cust/{username}/sys/stop");
        arrayString.add("/cust/{username}/sys/clearlock");
        arrayString.add("/cust/{username}/sys/start");
        arrayString.add("/cust/{username}/sys/lock");
        arrayString.add("/cust/{username}/sys/reopenssnsdata");
        arrayString.add("/cust/{username}/sys/custlist");
        arrayString.add("/cust/{username}/sys/cust/{customername}/status/{status}/substatus/{substatus}");

        return arrayString;
    }
////////////////////// 

    @RequestMapping(value = "/cust/{username}/id/{id}/regression", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<SsReport> getAllregression(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }

        ArrayList<SsReport> ret = afWebService.getSsReportRegression(username, idSt);
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

//    @RequestMapping(value = "/cust/{username}/id/{id}/regression/report/id/{pid}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
//    public @ResponseBody
//    ArrayList<ProdSummary> getAllregresreport(
//            @PathVariable("username") String username,
//            @PathVariable("id") String idSt,
//            @PathVariable("pid") String pidSt,
//            HttpServletRequest request, HttpServletResponse response
//    ) {
//        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
//        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
//            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
//            return null;
//        }
//
//        ArrayList<ProdSummary> ret = afWebService.getSsReportRegressionReport(username, idSt, pidSt);
//        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
//        return ret;
//    }
    @RequestMapping(value = "/cust/{username}/id/{id}/regression/start", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    int getAllregressionStart(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @RequestParam(value = "app", required = true) String appSt,
            @RequestParam(value = "url", required = true) String urlSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return 0;
        }
        String labURL = "";
        if (urlSt != null) {
            if (urlSt.length() > 0) {
                labURL = urlSt;
                if (urlSt.equals("locallab")) {
                    labURL = ServiceAFweb.URL_LOCALAB;
                }
            }
        }

        return afWebService.getSsReportRegressionStart(username, idSt, appSt, labURL);
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/regression/stop", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    int getAllregressionStop(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return 0;
        }

        return afWebService.getSsReportRegressionStop(username, idSt);
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/mon", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<SsReport> getAllmon(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }

        ArrayList<SsReport> ret = afWebService.getSsReportMon(username, idSt);
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

//    @RequestMapping(value = "/cust/{username}/id/{id}/mon/report/id/{pid}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
//    public @ResponseBody
//    ArrayList<ProdSummary> getAllmonreport(
//            @PathVariable("username") String username,
//            @PathVariable("id") String idSt,
//            @PathVariable("pid") String pidSt,
//            HttpServletRequest request, HttpServletResponse response
//    ) {
//        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
//        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
//            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
//            return null;
//        }
//
//        ArrayList<ProdSummary> ret = afWebService.getSsReportMonReport(username, idSt, pidSt);
//        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
//        return ret;
//    }
    @RequestMapping(value = "/cust/{username}/id/{id}/mon/execmonitor", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    int getAllmonExec(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return 0;
        }
        afWebService.getSsReportExectMon(username, idSt);
        return 1;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/mon/updatestatistic", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    int getAllmonStatistic(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return 0;
        }
        afWebService.getSsReportMonStatistic(username, idSt);
        return 1;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/mon/start", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    int getAllmonStart(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @RequestParam(value = "app", required = true) String appSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return 0;
        }

        return afWebService.getSsReportMonStart(username, idSt, appSt);
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/mon/stop", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    int getAllmonStop(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return 0;
        }

        return afWebService.getSsReportMonStop(username, idSt);
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/mon/pid", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<SsReport> getmonpidAll(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        ArrayList<SsReport> ret = afWebService.getSsReportAll(username, idSt);
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/mon/pid/{pid}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    SsReport getmonpid(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @PathVariable("pid") String pidSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        SsReport ret = afWebService.getSsReportById(username, idSt, pidSt);
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

////////////////////// 
    @RequestMapping(value = "/cust/{username}/id/{id}/serv", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<String> getAllprod(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @RequestParam(value = "length", required = false) String lengthSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        int length = 20; //10;
        if (lengthSt != null) {
            length = Integer.parseInt(lengthSt);
        }

        ArrayList<String> ret = afWebService.getSsnsprodAll(username, idSt, length);
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }
////////////////////// 
//////////////////////

/////////////////////    
    @RequestMapping(value = "/cust/{username}/id/{id}/serv/wlnpro/summary", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<ProdSummary> getwlnproprodSummary(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @RequestParam(value = "length", required = false) String lengthSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        int length = 20; //10;
        if (lengthSt != null) {
            length = Integer.parseInt(lengthSt);
        }
        ArrayList<ProdSummary> ret = afWebService.getSsnsprodSummary(username, idSt, length, SsnsService.APP_WLNPRO);
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/wlnpro", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<SsnsAcc> getwlnproprod(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @RequestParam(value = "length", required = false) String lengthSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        int length = 20; //10;
        if (lengthSt != null) {
            length = Integer.parseInt(lengthSt);
        }
        ArrayList<SsnsAcc> ret = afWebService.getSsnsprod(username, idSt, length, SsnsService.APP_WLNPRO);
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/wlnpro/id/{pid}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    SsnsAcc getwlnpropid(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @PathVariable("pid") String pidSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        SsnsAcc ret = afWebService.getSsnsprodById(username, idSt, pidSt, SsnsService.APP_WLNPRO);
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/wlnpro/id/{pid}/rt/downloadurl", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList getwlnprortsub(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @PathVariable("pid") String pidSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        String oper = SsnsService.APP_GET_DOWNURL;
        ArrayList<String> ret = afWebService.testSsnsprodWLNPROByIdRT(username, idSt, pidSt, SsnsService.APP_WLNPRO, oper, "");
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/wlnpro/featureall", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<String> getwlnprofeature(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        ArrayList<String> ret = afWebService.getSsnsprodByFeature(username, idSt, SsnsService.APP_WLNPRO);

        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/wlnpro/feature/name/summary", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<ProdSummary> getwlnprofeatureSummary(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @RequestParam(value = "name", required = true) String name,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        int length = 20; //10;     
        ArrayList<ProdSummary> ret = afWebService.getSsnsprodByFeatureNameSummary(username, idSt, name, SsnsService.APP_WLNPRO, length);

        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/wlnpro/feature/name", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<SsnsAcc> getwlnprofeature(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @RequestParam(value = "name", required = true) String name,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        int length = 20; //10;     
        ArrayList<SsnsAcc> ret = afWebService.getSsnsprodByFeatureName(username, idSt, name, SsnsService.APP_WLNPRO, length);

        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

/////////////////////////////////////     
    @RequestMapping(value = "/cust/{username}/id/{id}/serv/qual/summary", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<ProdSummary> getqualprodSummary(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @RequestParam(value = "length", required = false) String lengthSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        int length = 20; //10;
        if (lengthSt != null) {
            length = Integer.parseInt(lengthSt);
        }
        ArrayList<ProdSummary> ret = afWebService.getSsnsprodSummary(username, idSt, length, SsnsService.APP_QUAL);
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/qual", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<SsnsAcc> getqualprod(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @RequestParam(value = "length", required = false) String lengthSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        int length = 20; //10;
        if (lengthSt != null) {
            length = Integer.parseInt(lengthSt);
        }
        ArrayList<SsnsAcc> ret = afWebService.getSsnsprod(username, idSt, length, SsnsService.APP_QUAL);
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/qual/id/{pid}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    SsnsAcc getqualpid(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @PathVariable("pid") String pidSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        SsnsAcc ret = afWebService.getSsnsprodById(username, idSt, pidSt, SsnsService.APP_QUAL);
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/qual/id/{pid}/rt/availability", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList getqualavail(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @PathVariable("pid") String pidSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        String oper = SsnsService.QUAL_AVAL;
        ArrayList<String> ret = afWebService.testSsnsprodQualByIdRT(username, idSt, pidSt, SsnsService.APP_QUAL, oper, "");
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/qual/id/{pid}/rt/address_matches", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList getqualaddr(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @PathVariable("pid") String pidSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        String oper = SsnsService.QUAL_MATCH;
        ArrayList<String> ret = afWebService.testSsnsprodWLNPROByIdRT(username, idSt, pidSt, SsnsService.APP_QUAL, oper, "");
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/qual/featureall", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<String> getqualfeature(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        ArrayList<String> ret = afWebService.getSsnsprodByFeature(username, idSt, SsnsService.APP_QUAL);

        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/qual/feature/name/summary", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<ProdSummary> getqualfeatureSummary(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @RequestParam(value = "name", required = true) String name,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        int length = 20; //10;     
        ArrayList<ProdSummary> ret = afWebService.getSsnsprodByFeatureNameSummary(username, idSt, name, SsnsService.APP_QUAL, length);

        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/qual/feature/name", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<SsnsAcc> getqualfeature(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @RequestParam(value = "name", required = true) String name,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        int length = 20; //10;     
        ArrayList<SsnsAcc> ret = afWebService.getSsnsprodByFeatureName(username, idSt, name, SsnsService.APP_QUAL, length);

        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

/////////////////////    
    @RequestMapping(value = "/cust/{username}/id/{id}/serv/ttv/summary", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<ProdSummary> getttvcprodSummary(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @RequestParam(value = "length", required = false) String lengthSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        int length = 20; //10;
        if (lengthSt != null) {
            length = Integer.parseInt(lengthSt);
        }
        ArrayList<ProdSummary> ret = afWebService.getSsnsprodSummary(username, idSt, length, SsnsService.APP_TTVC);
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/ttv", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<SsnsAcc> getttvcprod(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @RequestParam(value = "length", required = false) String lengthSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        int length = 20; //10;
        if (lengthSt != null) {
            length = Integer.parseInt(lengthSt);
        }
        ArrayList<SsnsAcc> ret = afWebService.getSsnsprod(username, idSt, length, SsnsService.APP_TTVC);
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/ttv/id/{pid}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    SsnsAcc getttvcpid(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @PathVariable("pid") String pidSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        SsnsAcc ret = afWebService.getSsnsprodById(username, idSt, pidSt, SsnsService.APP_TTVC);
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/ttv/id/{pid}/rt/getcustomertvsubscription", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList getttvcrtsub(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @PathVariable("pid") String pidSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        String oper = SsnsService.TT_GetSub;
        ArrayList<String> ret = afWebService.testSsnsprodTTVCByIdRT(username, idSt, pidSt, SsnsService.APP_TTVC, oper, "");
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/ttv/id/{pid}/rt/validatewithauth", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList getttvcvrtvalidate(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @PathVariable("pid") String pidSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        String oper = SsnsService.TT_Vadulate;
        ArrayList<String> ret = afWebService.testSsnsprodTTVCByIdRT(username, idSt, pidSt, SsnsService.APP_TTVC, oper, "");
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/ttv/id/{pid}/rt/quotewithauth", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList getttvcrtquotation(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @PathVariable("pid") String pidSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        String oper = SsnsService.TT_Quote;
        ArrayList<String> ret = afWebService.testSsnsprodTTVCByIdRT(username, idSt, pidSt, SsnsService.APP_TTVC, oper, "");
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/ttv/featureall", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<String> getttvcfeature(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        ArrayList<String> ret = afWebService.getSsnsprodByFeature(username, idSt, SsnsService.APP_TTVC);

        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/ttv/feature/name/summary", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<ProdSummary> getttvcfeatureSummary(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @RequestParam(value = "name", required = true) String name,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        int length = 20; //10;     
        ArrayList<ProdSummary> ret = afWebService.getSsnsprodByFeatureNameSummary(username, idSt, name, SsnsService.APP_TTVC, length);

        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/ttv/feature/name", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<SsnsAcc> getttvcfeature(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @RequestParam(value = "name", required = true) String name,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        int length = 20; //10;     
        ArrayList<SsnsAcc> ret = afWebService.getSsnsprodByFeatureName(username, idSt, name, SsnsService.APP_TTVC, length);

        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

/////////////////////////////////////
    @RequestMapping(value = "/cust/{username}/id/{id}/serv/wifi/summary", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<ProdSummary> getwifiprodSummary(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @RequestParam(value = "length", required = false) String lengthSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        int length = 20; //10;
        if (lengthSt != null) {
            length = Integer.parseInt(lengthSt);
        }
        ArrayList<ProdSummary> ret = afWebService.getSsnsprodSummary(username, idSt, length, SsnsService.APP_WIFI);
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/wifi", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<SsnsAcc> getwifiprod(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @RequestParam(value = "length", required = false) String lengthSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        int length = 20; //10;
        if (lengthSt != null) {
            length = Integer.parseInt(lengthSt);
        }
        ArrayList<SsnsAcc> ret = afWebService.getSsnsprod(username, idSt, length, SsnsService.APP_WIFI);
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/wifi/id/{pid}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    SsnsAcc getwifipid(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @PathVariable("pid") String pidSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        SsnsAcc ret = afWebService.getSsnsprodById(username, idSt, pidSt, SsnsService.APP_WIFI);
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/wifi/id/{pid}/rt/rttest/getdevicestatus", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getprodwifiidrtTest(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @PathVariable("pid") String pidSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return "";
        }
        String oper = SsnsService.WI_GetDeviceStatus;
        String ret = afWebService.testSsnsprodWifiByIdRTTtest(username, idSt, pidSt, SsnsService.APP_PRODUCT, oper, "");
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/wifi/id/{pid}/rt/getdevicestatus", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList getwifiidrtdevstatus(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @PathVariable("pid") String pidSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        String oper = SsnsService.WI_GetDeviceStatus;
        ArrayList<String> ret = afWebService.testSsnsprodWifiByIdRT(username, idSt, pidSt, SsnsService.APP_WIFI, oper, "");
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }
    @RequestMapping(value = "/cust/{username}/id/{id}/serv/wifi/id/{pid}/rt/getdevices", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList getwifiidrtstatus(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @PathVariable("pid") String pidSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        String oper = SsnsService.WI_GetDevice;
        ArrayList<String> ret = afWebService.testSsnsprodWifiByIdRT(username, idSt, pidSt, SsnsService.APP_WIFI, oper, "");
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/wifi/id/{pid}/rt/getdeviceshdml", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList getwifiidrtstatushdml(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @PathVariable("pid") String pidSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        String oper = SsnsService.WI_GetDeviceHDML;
        ArrayList<String> ret = afWebService.testSsnsprodWifiByIdRT(username, idSt, pidSt, SsnsService.APP_WIFI, oper, "");
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/wifi/featureall", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<String> getwififeature(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        ArrayList<String> ret = afWebService.getSsnsprodByFeature(username, idSt, SsnsService.APP_WIFI);

        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/wifi/feature/name/summary", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<ProdSummary> getwififeatureSummary(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @RequestParam(value = "name", required = true) String name,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        int length = 20; //10;     
        ArrayList<ProdSummary> ret = afWebService.getSsnsprodByFeatureNameSummary(username, idSt, name, SsnsService.APP_WIFI, length);

        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/wifi/feature/name", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<SsnsAcc> getwififeature(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @RequestParam(value = "name", required = true) String name,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        int length = 20; //10;     
        ArrayList<SsnsAcc> ret = afWebService.getSsnsprodByFeatureName(username, idSt, name, SsnsService.APP_WIFI, length);

        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

/////////////////////////////////////    
    @RequestMapping(value = "/cust/{username}/id/{id}/serv/app/summary", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<ProdSummary> getappprodsummary(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @RequestParam(value = "length", required = false) String lengthSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        int length = 20; //10;
        if (lengthSt != null) {
            length = Integer.parseInt(lengthSt);
        }
        ArrayList<ProdSummary> ret = afWebService.getSsnsprodSummary(username, idSt, length, SsnsService.APP_APP);
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/app", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<SsnsAcc> getappprod(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @RequestParam(value = "length", required = false) String lengthSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        int length = 20; //10;
        if (lengthSt != null) {
            length = Integer.parseInt(lengthSt);
        }
        ArrayList<SsnsAcc> ret = afWebService.getSsnsprod(username, idSt, length, SsnsService.APP_APP);
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/app/id/{pid}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    SsnsAcc getappid(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @PathVariable("pid") String pidSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        SsnsAcc ret = afWebService.getSsnsprodById(username, idSt, pidSt, SsnsService.APP_APP);
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/app/id/{pid}/rt/getappointment", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList getappidrt(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @PathVariable("pid") String pidSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        String oper = SsnsService.APP_GET_APP;
        ArrayList<String> ret = afWebService.testSsnsprodAppByIdRT(username, idSt, pidSt, SsnsService.APP_APP, oper, "");
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/app/id/{pid}/rt/searchtimeslot", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList getappidrtTimeSlot(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @PathVariable("pid") String pidSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        String oper = SsnsService.APP_GET_TIMES;
        ArrayList<String> ret = afWebService.testSsnsprodAppByIdRT(username, idSt, pidSt, SsnsService.APP_APP, oper, "");
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/app/featureall", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<String> getappfeature(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        ArrayList<String> ret = afWebService.getSsnsprodByFeature(username, idSt, SsnsService.APP_APP);

        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/app/feature/name/summary", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<ProdSummary> getappfeatureSummary(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @RequestParam(value = "name", required = true) String name,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        int length = 20; //10;

        ArrayList<ProdSummary> ret = afWebService.getSsnsprodByFeatureNameSummary(username, idSt, name, SsnsService.APP_APP, length);

        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/app/feature/name", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<SsnsAcc> getappfeature(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @RequestParam(value = "name", required = true) String name,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        int length = 20; //10;

        ArrayList<SsnsAcc> ret = afWebService.getSsnsprodByFeatureName(username, idSt, name, SsnsService.APP_APP, length);

        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }
////////////////////////////////////

    @RequestMapping(value = "/cust/{username}/id/{id}/serv//prod/summary", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<ProdSummary> getprodSummary(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @RequestParam(value = "length", required = false) String lengthSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        int length = 20; //10;
        if (lengthSt != null) {
            length = Integer.parseInt(lengthSt);
        }
        ArrayList<ProdSummary> ret = afWebService.getSsnsprodSummary(username, idSt, length, SsnsService.APP_PRODUCT);
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/prod", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<SsnsAcc> getprodttv(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @RequestParam(value = "length", required = false) String lengthSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        int length = 20; //10;
        if (lengthSt != null) {
            length = Integer.parseInt(lengthSt);
        }
        ArrayList<SsnsAcc> ret = afWebService.getSsnsprod(username, idSt, length, SsnsService.APP_PRODUCT);
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/prod/id/{pid}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    SsnsAcc getprodttvid(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @PathVariable("pid") String pidSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        SsnsAcc ret = afWebService.getSsnsprodById(username, idSt, pidSt, SsnsService.APP_PRODUCT);
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/prod/id/{pid}/rt/rttest/getproductbyid", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getprodttvidrtTest(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @PathVariable("pid") String pidSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return "";
        }

        String ret = afWebService.testSsnsprodByIdRTtest(username, idSt, pidSt, SsnsService.APP_PRODUCT, "", "");
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/prod/id/{pid}/rt/callcontrol", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList getprodCCrtTest(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @PathVariable("pid") String pidSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        ArrayList<String> ret = afWebService.testSsnsprodByIdRT(username, idSt, pidSt, SsnsService.APP_PRODUCT, SsnsService.PROD_GET_CC, "");
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/prod/id/{pid}/rt/getproductbyid", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList getprodttvidrt(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @PathVariable("pid") String pidSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }

        ArrayList<String> ret = afWebService.testSsnsprodByIdRT(username, idSt, pidSt, SsnsService.APP_PRODUCT, "", "");
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/prod/featureall", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<String> getprodttvfeature(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        ArrayList<String> ret = afWebService.getSsnsprodByFeature(username, idSt, SsnsService.APP_PRODUCT);

        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/prod/feature/name/summary", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<ProdSummary> getprodttvfeatureNameSummary(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @RequestParam(value = "name", required = true) String name,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        int length = 20; //10;
        ArrayList<ProdSummary> ret = afWebService.getSsnsprodByFeatureNameSummary(username, idSt, name, SsnsService.APP_PRODUCT, length);
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/id/{id}/serv/prod/feature/name", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<SsnsAcc> getprodttvfeatureName(
            @PathVariable("username") String username,
            @PathVariable("id") String idSt,
            @RequestParam(value = "name", required = true) String name,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        }
        int length = 20; //10;
        ArrayList<SsnsAcc> ret = afWebService.getSsnsprodByFeatureName(username, idSt, name, SsnsService.APP_PRODUCT, length);

        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }
//////////////////////////////////////////////

    @RequestMapping(value = "/ping", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    WebStatus serverPing() {
        WebStatus msg = new WebStatus();

        msg = afWebService.serverPing();
        return msg;
    }

    //////////////
    @RequestMapping(value = "/cust/{username}/sys/resetdb", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    WebStatus SystemResetDB(@PathVariable("username") String username) {
        WebStatus msg = new WebStatus();
        // remote is stopped
        if (CKey.UI_ONLY == true) {
            return null;
        }
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            if (username.toLowerCase().equals(CKey.ADMIN_USERNAME.toLowerCase())) {
                msg.setResponse(afWebService.SystemRestDBData());
                msg.setResult(true);
                return msg;
            }
        }
        CustomerObj cust = afWebService.getCustomerIgnoreMaintenance(username, null);
        if (cust != null) {
            if (cust.getType() == CustomerObj.INT_ADMIN_USER) {
                msg.setResponse(afWebService.SystemRestDBData());
                msg.setResult(true);
                return msg;
            }
        }
        return null;
    }

    @RequestMapping(value = "/cust/{username}/sys/stop", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    WebStatus SystemStop(@PathVariable("username") String username) {
        WebStatus msg = new WebStatus();

        if (username.toLowerCase().equals(CKey.ADMIN_USERNAME.toLowerCase())) {
            msg.setResponse(afWebService.SystemStop());
            msg.setResult(true);
            return msg;
        }

        return null;
    }

    @RequestMapping(value = "/cust/{username}/sys/start", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    WebStatus SystemStart(@PathVariable("username") String username) {
        WebStatus msg = new WebStatus();
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            if (username.toLowerCase().equals(CKey.ADMIN_USERNAME.toLowerCase())) {
                msg.setResponse(afWebService.SystemStart());
                msg.setResult(true);
                return msg;
            }
        }

        CustomerObj cust = afWebService.getCustomerIgnoreMaintenance(username, null);
        if (cust != null) {
            if (cust.getType() == CustomerObj.INT_ADMIN_USER) {
                msg.setResponse(afWebService.SystemStart());
                msg.setResult(true);
                return msg;
            }
        }
        return null;
    }

    @RequestMapping(value = "/cust/{username}/sys/backupssnsacc", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    WebStatus SystemBackupacc(@PathVariable("username") String username) {
        WebStatus msg = new WebStatus();
        // remote is stopped
        CustomerObj cust = afWebService.getCustomerIgnoreMaintenance(username, null);
        if (cust != null) {
            if (cust.getType() == CustomerObj.INT_ADMIN_USER) {
                msg.setResponse(afWebService.systemBackupSsnsAcc() + "");
                msg.setResult(true);
                return msg;
            }
        }

        return null;
    }

    // using server/restoressnsacc
//    @RequestMapping(value = "/cust/{username}/sys/restoressnsacc", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
//    public @ResponseBody
//    WebStatus SystemRestoreacc(@PathVariable("username") String username) {
//        WebStatus msg = new WebStatus();
//        // remote is stopped
//        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
//            if (username.toLowerCase().equals(CKey.ADMIN_USERNAME.toLowerCase())) {
//                return null;
//            }
//        }
//        CustomerObj cust = afWebService.getCustomerIgnoreMaintenance(username, null);
//        if (cust != null) {
//            if (cust.getType() == CustomerObj.INT_ADMIN_USER) {
//                msg.setResponse(afWebService.systemRestoresSsnsAcc() + "");
//                msg.setResult(true);
//                return msg;
//            }
//        }
//
//        return null;
//    }
    @RequestMapping(value = "/cust/{username}/sys/reopenssnsdata", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    WebStatus SystemReopen(@PathVariable("username") String username) {
        WebStatus msg = new WebStatus();
        // remote is stopped
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            if (username.toLowerCase().equals(CKey.ADMIN_USERNAME.toLowerCase())) {
                msg.setResponse(afWebService.SystemClearLock());
                msg.setResult(true);
                return msg;
            }
        }
        CustomerObj cust = afWebService.getCustomerIgnoreMaintenance(username, null);
        if (cust != null) {
            if (cust.getType() == CustomerObj.INT_ADMIN_USER) {
                msg.setResponse(afWebService.SystemReOpenData());
                msg.setResult(true);
                return msg;
            }
        }

        return null;
    }

    @RequestMapping(value = "/cust/{username}/sys/clearlock", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    WebStatus SystemClearLock(@PathVariable("username") String username) {
        WebStatus msg = new WebStatus();
        // remote is stopped
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            if (username.toLowerCase().equals(CKey.ADMIN_USERNAME.toLowerCase())) {
                msg.setResponse(afWebService.SystemClearLock());
                msg.setResult(true);
                return msg;
            }
        }
        CustomerObj cust = afWebService.getCustomerIgnoreMaintenance(username, null);
        if (cust != null) {
            if (cust.getType() == CustomerObj.INT_ADMIN_USER) {
                msg.setResponse(afWebService.SystemClearLock());
                msg.setResult(true);
                return msg;
            }
        }

        return null;
    }

    @RequestMapping(value = "/cust/{username}/sys/lock", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList getLockAll(
            @PathVariable("username") String username
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);

        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            if (username.toLowerCase().equals(CKey.ADMIN_USERNAME.toLowerCase())) {
                ArrayList result = afWebService.getAllLock();
                return result;
            }
        }

        CustomerObj cust = afWebService.getCustomerPassword(username, null);
        if (cust != null) {
            if (cust.getType() == CustomerObj.INT_ADMIN_USER) {
                ArrayList result = afWebService.getAllLock();
                ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
                return result;
            }
        }
        return null;
    }

    @RequestMapping(value = "/cust/{username}/sys/clearallreport", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    int getAllmonreport(
            @PathVariable("username") String username,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return 0;
        }

        int ret = afWebService.SsReportClearAll(username);
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return ret;
    }

    @RequestMapping(value = "/cust/{username}/sys/request", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    RequestObj SystemSQLRequest(
            @PathVariable("username") String username,
            @RequestBody String input
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        RequestObj sqlReq = null;
        try {
            sqlReq = new ObjectMapper().readValue(input, RequestObj.class);
        } catch (IOException ex) {
            return null;
        }

        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            if (username.toLowerCase().equals(CKey.ADMIN_USERNAME.toLowerCase())) {
                RequestObj sqlResp = afWebService.SystemSQLRequest(sqlReq);
                return sqlResp;
            }
        }

        CustomerObj cust = afWebService.getCustomerPassword(username, null);
        if (cust != null) {
            if (cust.getType() == CustomerObj.INT_ADMIN_USER) {
                RequestObj sqlResp = afWebService.SystemSQLRequest(sqlReq);
                ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
                return sqlResp;
            }
        }
        return null;
    }

//////////////
    @RequestMapping(value = "/cust/{username}/sys/custlist", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList getCustList(
            @PathVariable("username") String username,
            @RequestParam(value = "length", required = false) String lengthSt) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        int length = 0; //20;
        if (lengthSt != null) {
            length = Integer.parseInt(lengthSt);
        }
        CustomerObj cust = afWebService.getCustomerPassword(username, null);
        if (cust != null) {
            if (cust.getType() == CustomerObj.INT_ADMIN_USER) {
                ArrayList custNameList = afWebService.getCustomerList(length);
                ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
                return custNameList;
            }
        }
        return null;
    }

    @RequestMapping(value = "/cust/{username}/sys/cust/{customername}/status/{status}/substatus/{substatus}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    int updateCustomer(
            @PathVariable("username") String username,
            @PathVariable("customername") String customername,
            @PathVariable("status") String status,
            @PathVariable("substatus") String substatus
    ) {

        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (customername == null) {
            return 0;
        }
        CustomerObj cust = afWebService.getCustomerPassword(username, null);
        if (cust != null) {
            if (cust.getType() == CustomerObj.INT_ADMIN_USER) {
                int result = afWebService.updateCustStatusSubStatus(customername, status, substatus);
                ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
                return result;
            }
        }
        return 0;
    }

    /////////////////////////////////////////////////////////////////////////    
    @RequestMapping(value = "/timer")
    public ModelAndView timerPage() {
        ModelAndView model = new ModelAndView("helloWorld");

        model.addObject("message", AFwebService.getServerObj().getServerName() + " " + AFwebService.getServerObj().getVerString() + "</br>"
                + AFwebService.getServerObj().getLastServUpdateESTdate() + "</br>"
                + AFwebService.getServerObj().getTimerMsg() + "</br>" + AFwebService.getServerObj().getTimerThreadMsg());
        return model;
    }
///////////////////////

    @RequestMapping(value = "/cust/{username}/sys/mysql", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getmysql(
            @PathVariable("username") String username,
            @RequestBody String input
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        RequestObj sqlObj = new RequestObj();
        try {
            sqlObj = new ObjectMapper().readValue(input, RequestObj.class);
        } catch (IOException ex) {
            return "";
        }
        CustomerObj cust = afWebService.getCustomerIgnoreMaintenance(username, null);
        if (cust != null) {
            if (cust.getType() == CustomerObj.INT_ADMIN_USER) {
//                System.out.println(sqlObj.getReq());
                if (sqlObj.getCmd().equals("1")) {
                    return afWebService.SystemRemoteGetMySQL(sqlObj.getReq());
                } else if (sqlObj.getCmd().equals("2")) {
                    return afWebService.SystemRemoteUpdateMySQL(sqlObj.getReq());
                } else if (sqlObj.getCmd().equals("3")) {
                    return afWebService.SystemRemoteUpdateMySQLList(sqlObj.getReq());
                }
            }
        }
        return "";
    }

    @RequestMapping(value = "/server/sendurl", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList<String> setServerSendURL(
            @RequestParam(value = "url", required = true) String urlSt,
            HttpServletRequest request, HttpServletResponse response
    ) {

        return afWebService.ServerSendURL(urlSt);
    }

    @RequestMapping(value = "/server/restoressnsacc", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    int getServerResourceAccObj() {

        return afWebService.systemRestoresSsnsAcc();
    }

    @RequestMapping(value = "/server", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ArrayList getServerObj() {

        return afWebService.getServerList();
    }

    @RequestMapping(value = "/server/mysqldb", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getServerLocalDbURL() {
        return ServiceAFweb.URL_LOCAL_DB;
    }

    @RequestMapping(value = "/server/mysqldb/set", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String setServerLocalDbURL(
            @RequestParam(value = "url", required = true) String urlSt,
            HttpServletRequest request, HttpServletResponse response
    ) {

        ServiceAFweb.URL_LOCAL_DB = urlSt.trim();
        //restart ServiceAFweb
        afWebService.SystemStart();
        return "done...";
    }

    @RequestMapping(value = "/server/filepath", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getServerFileP() {
        return ServiceAFweb.FileLocalPath;
    }

    @RequestMapping(value = "/server/sysfilepath", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getServerFileDir() {
        String userDirectory = Paths.get("").toAbsolutePath().toString();
        return userDirectory;
    }

    @RequestMapping(value = "/server/filepath/set", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String setServerfileP(
            @RequestParam(value = "path", required = true) String pathSt,
            HttpServletRequest request, HttpServletResponse response
    ) {

        ServiceAFweb.FileLocalPath = pathSt.trim();
        return "done...";
    }

    @RequestMapping(value = "/server/url0", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getServerURL() {
        String url0 = RESTtimer.serverURL_0;
        if (url0.length() == 0) {
            url0 = ServiceAFweb.SERVERDB_URL;
        }
        return url0;
    }

    @RequestMapping(value = "/server/url0/set", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String setServerURL(
            @RequestParam(value = "url", required = true) String urlSt,
            HttpServletRequest request, HttpServletResponse response
    ) {

        RESTtimer.serverURL_0 = urlSt.trim();
        return "done...";
    }

    @RequestMapping(value = "/timerhandler", produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    WebStatus timerHandlerREST(
            @RequestParam(value = "resttimerMsg", required = false) String resttimerMsg
    ) {

        WebStatus msg = new WebStatus();
        msg.setResult(true);
        msg.setResultID(ConstantKey.ENABLE);

        //process timer handler
        int timerCnt = afWebService.timerHandler(resttimerMsg);

        msg.setResponse("timerCnt " + timerCnt);
        return msg;
    }

    @RequestMapping(value = "/cust/login", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    LoginObj getCustObjLogin(
            @RequestParam(value = "email", required = true) String emailSt,
            @RequestParam(value = "pass", required = true) String passSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            LoginObj loginObj = new LoginObj();
            loginObj.setCustObj(null);
            WebStatus webStatus = new WebStatus();
            webStatus.setResultID(100);
            loginObj.setWebMsg(webStatus);
            return loginObj;
        }
        if (emailSt == null) {
            return null;
        }
        if (passSt == null) {
            return null;
        }
        LoginObj loginObj = afWebService.getCustomerEmailLogin(emailSt, passSt);
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return loginObj;
    }

    @RequestMapping(value = "/cust/{username}/login", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    LoginObj getCustObjUserLogin(
            @PathVariable("username") String username,
            @RequestParam(value = "pass", required = true) String passSt,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ServiceAFweb.getServerObj().setCntControRequest(ServiceAFweb.getServerObj().getCntControRequest() + 1);
        if (ServiceAFweb.getServerObj().isSysMaintenance() == true) {
            LoginObj loginObj = new LoginObj();
            loginObj.setCustObj(null);
            WebStatus webStatus = new WebStatus();
            webStatus.setResultID(100);
            loginObj.setWebMsg(webStatus);
            return loginObj;
        }
        if (passSt == null) {
            return null;
        }
        LoginObj loginObj = afWebService.getCustomerLogin(username, passSt);
        ServiceAFweb.getServerObj().setCntControlResp(ServiceAFweb.getServerObj().getCntControlResp() + 1);
        return loginObj;
    }

}
