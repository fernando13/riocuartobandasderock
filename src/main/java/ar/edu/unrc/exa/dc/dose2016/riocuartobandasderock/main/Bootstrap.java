package ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.main;

import static spark.Spark.*;
import static ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.main.JsonUtil.json;

import java.nio.file.Files;
import java.nio.file.Paths;

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


	private static BandController bands;
	private static ArtistController artistController;
	private static AlbumController albumController;
    private static UserController userController;
	private static SongController songController;
	private static BandMemberController bandMemberController;

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

        albumController  = AlbumController.getInstance();
        artistController = ArtistController.getInstance();
        bandMemberController = BandMemberController.getInstance();
        bands = BandController.getInstance();
        songController = new SongController();
        userController = UserController.getInstance();
        port(Integer.parseInt(ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.main.ServerOptions.getInstance().getAppPort()));

        before("/bands", (req, res) -> {
            if (!userController.authenticated(req, res)) {
                halt(401, "Access forbidden\n");
            }
        });

        // List of route and verbs API REST

        post("/albums/", (req, res) -> albumController.create(req, res));

        get("/hello", (req, res) -> "Hello World");

        /**
        *   Band routes
        */
        get("/bands",(req, res) -> bands.getBands(req, res));

        get("/bands/findbyname/:name",(req, res) -> bands.getBandByName(req, res));
        
        get("/bands/findbygenre/:genre",(req, res) -> bands.getBandByGenre(req, res));
        
        get("/bands/find/",(req, res) -> bands.getBandByNameAndGenre(req, res));

        post("/bands/",(req, res) -> bands.createBand(req, res));

        put("/bands",(req, res) -> bands.updateBand(req, res));

        delete("/bands/:name",(req, res) -> bands.deleteBand(req, res));
        
        /* ArtistController  Begin Routes*/
        
        /** returns an artist whose id = :id the output is json format
         * example:   Request: GET /artist/10
         *            Output : {name: Matias, surname: Cerra, nickname: }
         * 
         * **/    
        get("/artists/:id",(req,res)->artistController.getArtistById(req,res));
                                
        get("/artists/findbyallattributes/",(req,res)->artistController.getOneArtist(req,res),json());
                
        get("/artists",(req,res)->artistController.getAllArtists(req,res),json());

        get("/artists/findbyname/:name",(req,res)->artistController.getArtistByName(req,res),json());
        
        get("/artists/findbynickname/:nickname",(req,res)->artistController.getArtistByNickname(req,res),json());

        get("/artists/findbysurname/:surname",(req,res)->artistController.getArtistBySurname(req,res),json());
                        
        get("/artists/getbands/",(req,res)->artistController.getBandMembersByArtist(req, res),json());
        
        get("/artists/getbandsbyId/:artistID",(req,res)->artistController.getBandMembersByArtistId(req, res),json());

        // the files statics for artist
        get("/artistadmin/:page", (req, res) -> {
        	String page=req.params("page");
        	String content;
        	try	{
        		content = new String(Files.readAllBytes(Paths.get("src/main/webapp/artist-"+page+".html")));
        	}catch (Exception e) {
        		content = "The file Html not exist or error loading this";
        	}
            res.status(200);
            res.header("Server", "My Spark Server");
            res.header("Connection","close");
            res.header("Content-Type", "text/html; charset=utf-8");
            res.header("Content-Length", Integer.toString(content.length()));
            res.body(content);
            return null;
        });

        post("/artists",(req,res)->artistController.createArtist(req,res));
        
        put("/artists/:id",(req,res)->artistController.updateArtist(req,res));

        delete("/artists/:id",(req,res)->artistController.deleteArtist(req,res));
        
        /**
         * BandMember routes
         */
        post("/bandmembers/",(req,res)->bandMemberController.createBandMember(req, res));
        
        delete("/bandmembers/:artistID/:BandID",(req,res)->bandMemberController.deleteBandMember(req, res));
        
        /**
         * Users routes
         */
        post("/users", (req, res) -> userController.create(req, res));
        put("/users/:name", (req, res) -> userController.update(req, res));
        delete("/users/:name", (req, res) -> userController.delete(req, res));
        post("/login", (req, res) -> userController.login(req, res));
        post("/logout", (req, res) -> userController.logout(req, res));

        post("/song",(req,res)->songController.create(req, res));

        get("/songs/findbyname/:name",(req,res)->songController.getSongByName(req,res));
        
        get("/songs/findbyduration/:duration",(req,res)->songController.getSongByDuration(req,res));
        
        delete("/song/:id",(req, res) -> songController.removeSong(req, res));
        
        after((req, res) -> {/*res.type("application/json");*/});

    }
}
