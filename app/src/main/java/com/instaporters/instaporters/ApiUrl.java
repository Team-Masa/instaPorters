package com.instaporters.instaporters;

/**
 * Created by maheshkumar on 3/6/16.
 */
public class ApiUrl {
    public static String get_all_jobs() {
        return "http://192.168.6.174:1337/unassigned-jobs?";
    }
    public static String assign_job_to_porter() {
        return "http://192.168.6.174:1337/assign-porter-to-job";
    }
    public static String porter_start() {
        return "http://192.168.6.174:1337/porter-start";
    }
    public static String porter_end() {
        return "http://192.168.6.174:1337/porter-end";
    }
    public static String porters() {
        return "http://192.168.6.174:1337/porters";
    }
}
