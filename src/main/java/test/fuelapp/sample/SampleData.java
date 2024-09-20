package test.fuelapp.sample;

import java.util.ArrayList;
import java.util.List;

public class SampleData {
    public static List<StationDetails> getSampleData() {
        List<StationDetails> stations = new ArrayList<>();

        // 20 fuel stations in Australia
        stations.add(new StationDetails("", "N/A", "1", "BP Brisbane", "123 Brisbane St, Brisbane QLD 4000", "-27.4698", "153.0251"));
        stations.add(new StationDetails("", "N/A", "2", "Caltex Sydney", "456 Sydney St, Sydney NSW 2000", "-33.8651", "151.2099"));
        stations.add(new StationDetails("", "N/A", "3", "Shell Melbourne", "789 Melbourne St, Melbourne VIC 3000", "-37.8136", "144.9631"));
        stations.add(new StationDetails("", "N/A", "4", "BP Brisbane", "321 Brisbane St, Brisbane QLD 4000", "-27.4698", "153.0251"));
        stations.add(new StationDetails("", "N/A", "5", "Caltex Perth", "901 Perth St, Perth WA 6000", "-31.9524", "115.8605"));
        stations.add(new StationDetails("", "N/A", "N/A", "Shell Adelaide", "234 Adelaide St, Adelaide SA 5000", "-34.9285", "138.5999"));
        stations.add(new StationDetails("", "N/A", "N/A", "BP Canberra", "567 Canberra St, Canberra ACT 2600", "-35.2809", "149.1300"));
        stations.add(new StationDetails("", "N/A", "N/A", "Caltex Hobart", "890 Hobart St, Hobart TAS 7000", "-42.8821", "147.3272"));
        stations.add(new StationDetails("", "N/A", "N/A", "Shell Darwin", "345 Darwin St, Darwin NT 0800", "-12.4634", "130.8456"));
        stations.add(new StationDetails("", "N/A", "N/A", "BP Brisbane", "678 Brisbane St, Brisbane QLD 4000", "-27.4698", "153.0251"));
        stations.add(new StationDetails("", "N/A", "N/A", "Caltex Sydney", "567 Sydney St, Sydney NSW 2000", "-33.8651", "151.2099"));
        stations.add(new StationDetails("", "N/A", "N/A", "Shell Melbourne", "901 Melbourne St, Melbourne VIC 3000", "-37.8136", "144.9631"));
        stations.add(new StationDetails("", "N/A", "N/A", "BP Perth", "234 Perth St, Perth WA 6000", "-31.9524", "115.8605"));
        stations.add(new StationDetails("", "N/A", "N/A", "Caltex Adelaide", "456 Adelaide St, Adelaide SA 5000", "-34.9285", "138.5999"));
        stations.add(new StationDetails("", "N/A", "N/A", "Shell Canberra", "789 Canberra St, Canberra ACT 2600", "-35.2809", "149.1300"));
        stations.add(new StationDetails("", "N/A", "N/A", "BP Hobart", "567 Hobart St, Hobart TAS 7000", "-42.8821", "147.3272"));
        stations.add(new StationDetails("", "N/A", "N/A", "Caltex Darwin", "345 Darwin St, Darwin NT 0800", "-12.4634", "130.8456"));
        stations.add(new StationDetails("", "N/A", "N/A", "Shell Brisbane", "123 Brisbane St, Brisbane QLD 4000", "-27.4698", "153.0251"));
        stations.add(new StationDetails("", "N/A", "N/A", "BP Sydney", "901 Sydney St, Sydney NSW 2000", "-33.8651", "151.2099"));
        stations.add(new StationDetails("", "N/A", "N/A", "Caltex Melbourne", "456 Melbourne St, Melbourne VIC 3000", "-37.8136", "144.9631"));

        return stations;
    }
}