package controllers;

import play.mvc.*;

import views.html.*;

/**
 * Created by roye on 2017/4/24.
 */
public class CeateBackendController extends Controller{

    public Result HelloWorld()
    {

        return ok("HelloWorld");
    }
    public Result getUserInformation()
    {
        return null;
    }
}
