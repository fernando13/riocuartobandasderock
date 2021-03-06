package ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.main;

import static spark.Spark.*;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.dao.impl.BandDaoImpl;

/**
 * 
 * @author Dose 
 *
 */
public class Bootstrap {


	private static BandController bands = new BandController(new BandDaoImpl());
	private static ArtistController artistController;
	private static AlbumController albumController = AlbumController.getInstance();
	private static SongController songController;

    public static void main(String[] args) {

    	CommandLineParser parser = new DefaultParser();
    	
    	Option dbHost = new Option("dbh","dbHost",true,"use given host as database host");
    	Option dbPort = new Option("dbp","dbPort",true,"use given port as database port");
    	Option appPort =  new Option("ap","appPort",true,"use given port as application port");
    	appPort.setRequired(false);
    	
    	Options options = new Options();
    	options.addOption(dbHost);
    	options.addOption(dbPort);
    	options.addOption(appPort);
        try {
            // parse the command line arguments
            CommandLine line = parser.parse( options, args );
            if (line.hasOption("dbHost")) {
            		ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.main.ServerOptions.getInstance().setDbHost(line.getOptionValue("dbHost"));
            }
            if (line.hasOption("dbPort")) {
            	ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.main.ServerOptions.getInstance().setDbPort(line.getOptionValue("dbPort"));
            }
            
            if (line.hasOption("appPort")) { 
            	ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.main.ServerOptions.getInstance().setAppPort(line.getOptionValue("appPort"));            
            }
        }
        catch( ParseException exp ) {
            // oops, something went wrong
            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
        }

        /* TABLE CODE  RESPONSE HTTP 
         * ===============================================================
         * WHEN ACTION PASS INVALID ARGUMENT RETURN                      CODE 400
         * INSERT ONE NEW REGISTER IS OK                                 CODE 201
         * INSERT ONE NEW REGISTER IS DATABASE ERROR                     CODE 409
         * 
         * SEARCH REGISTER ALL OR FOR ATRIBUTTE AND RETURN NO EMPTY LIST CODE 200
         * SEARCH REGISTER ALL OR FOR ATRIBUTTE AND RETURN EMPTY  LIST   CODE 204
         *
         */
        
        // List of controller

        
        artistController = ArtistController.getInstance();

        songController = new SongController();
        port(Integer.parseInt(ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.main.ServerOptions.getInstance().getAppPort()));


        // List of route and verbs API REST
        
        post("/albums/", (req, res) -> albumController.create(req, res));

        get("/hello", (req, res) -> "Hello World");

        get("/bands",(req, res) -> bands.getBands(req, res));

        get("/band/:name",(req, res) -> bands.getBandByName(req, res));

        post("/band/",(req, res) -> bands.createBand(req, res));

        put("/band",(req, res) -> bands.updateBand(req, res));

        delete("/band/:name",(req, res) -> bands.deleteBand(req, res));
        
        /* ArtistController BEGIN List of Routes */
        
        get ("/artist", (req,res)->artistController.getAllArtists(req,res));

        get("/artist/findbyname/:name",(req,res)->artistController.getArtistByName(req,res));
        
        get("/artist/findbynickname/:nickname",(req,res)->artistController.getArtistByNickname(req,res));

        get("/artist/findbysurname/:surname",(req,res)->artistController.getArtistBySurname(req,res));

        /* ArtistController END List of  Routes */
        
        
        post("/artist/",(req,res)->artistController.createArtist(req,res));
        
        post("/song/",(req,res)->songController.addSong(req, res));

        get("/song/findbyname/:name",(req,res)->songController.getSongByName(req,res));
        
        get("/song/findbyduration/:name",(req,res)->songController.getSongByDuration(req,res));
        
        
    }
}
