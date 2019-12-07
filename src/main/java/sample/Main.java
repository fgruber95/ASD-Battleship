package sample;

import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class Main extends Application {
    private static final Logger logger = Logger.getLogger(Main.class);

    // JDBC driver name and database URL
    static final String DB_URL = "jdbc:h2:mem:battleshipDB";

    //  Database credentials
    static final String USER = "sa";
    static final String PASS = "";


    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("Starting game");
        System.out.println(new Exception());

        initializeDB();
        Game game = new Game(primaryStage);
    }


    private void executeDBQuery(Connection connection, String statement) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute(statement);
        stmt.close();
        logger.debug("Executed SQL Query");
    }

    private void initializeDB() {
        Connection conn = null;
        try {
            //STEP 2: Open a connection
            logger.info("Initializing database..." + conn.getClientInfo());
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            executeDBQuery(conn, "CREATE TABLE IF NOT EXISTS BShips " +
                    "(bid INTEGER not NULL, " +
                    " source VARCHAR(255), " +
                    " PRIMARY KEY ( bid ))");
            //STEP 3: Execute a query
            executeDBQuery(conn, "CREATE TABLE IF NOT EXISTS ImageShip " +
                    "(iid INTEGER not NULL, " +
                    " bShipId INTEGER, " +
                    " x INTEGER, " +
                    " y INTEGER, " +
                    " length INTEGER, " +
                    " PRIMARY KEY ( iid ), " +
                    " FOREIGN KEY ( bShipId ) REFERENCES BShips( bid ))");

            // STEP 4: Clean-up environment
            conn.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            logger.error(se.getMessage());
            logger.error(se.getStackTrace());
        } catch (Exception e) {
            //Handle errors for Class.forName
            logger.error(e.getCause());
            logger.error(e.getStackTrace());
        } finally {
            //finally block used to close resources
            try {
                if (conn != null) conn.close();
                logger.debug("Closed database connection.");
            } catch (SQLException se) {
                logger.error(se.getSQLState());
                logger.error(se.getStackTrace());
            } //end finally try
        } //end try
    }


<<<<<<< HEAD
    /*Wir berechnen x und y relativ zum jeweiligen spielfeld und kriegen eine zahl zwischen 0 und 9 raus.*/
    private int[] calculateXY(int imageshipx, int imageshipy, int p1x, int p1y, int p2x, int p2y) {
        int[] result = new int[2];

        //Checkt ob die Koordinaten vom Schiff im richtigen Feld liegen
        if (imageshipx >= p1x && imageshipx <= p2x && imageshipy >= p1y && imageshipy <= p2y) {
            int vectorx;
            int vectory;
            //berechnet Relation zum Spielfeld
            vectorx = imageshipx - p1x;
            vectory = imageshipy - p1y;
            //Damit es eine Zahl zwischen 0 und 9 ist (denke ich!!)
            result[0] = vectorx / 40;
            result[1] = vectory / 40;
            return result;
        }
        return null;
    }


    private void saveShips(ImageShip[] imageShip, Player player, int p1x, int p2x) {
        logger.info("save Ships");
        /*Geht alle Schiffe duch und schaut erstmal ob */
        for (ImageShip imageship : imageShip) {
            if (!imageship.isDisable()) {
                int[] a = calculateXY(imageship.getX(), imageship.getY(), p1x, 560, p2x, 960);

                if (a != null) {
                    if (player.playfield.setShip(a[0], a[1], imageship.getLength(), imageship.getDirection(), imageship.getDiffvectorx(), imageship.getDiffvectory())) {
                        imageship.lock();

                    } else {
                        imageship.changePosition(0, 0);
                        imageship.rotateTo(Direction.RIGHT);
                    }
                } else {
                    imageship.changePosition(0, 0);
                    imageship.rotateTo(Direction.RIGHT);

                }
            }
        }
        if (player.playfield.isFleetComplete()) {
            gameRound++;
            if (player == player1) {
                changeMask();
                buttonSaveShipsLeft.setVisible(false);


            } else {
                buttonSaveShipsRight.setVisible(false);
                changeMask();
                buttonSeeShips1.setVisible(true);
                buttonSeeShips2.setVisible(true);
                indicate1.setVisible(true);


            }
            if (player1.playfield.isFleetComplete() && player2.playfield.isFleetComplete()) {
                activateMask();
            }

        }
    }

    private void attacks(int x, int y) {
        int[] a;
        if (!(player1.playfield.gameOver() || player2.playfield.gameOver()) && shipsComplete) {
            logger.info("Schiffe fertig");
            if (gameRound % 2 == 1) {
                a = calculateXY(x, y, 440 + 40, 40 + 40, 440 + 440, 440 + 40);

                if (a != null && player1.isAttackPossible(new Point(a[0], a[1]))) {
                    if (player2.playfield.attack(a[0], a[1])) {
                        drawAttack(a[0], a[1], x, y, player2);
                        player1.saveAttack(new Point(a[0], a[1]));
                        activateMask();
                        bombplay.stop();
                        bombplay.play();

                    } else {
                        drawMiss(x, y);
                        player1.saveAttack(new Point(a[0], a[1]));
                        activateMask();
                        indicate1.setVisible(false);
                        indicate2.setVisible(true);
                        missplay.stop();
                        missplay.play();
                    }
                }
                if (player2.playfield.gameOver()) {
                    logger.info("Spieler 1 hat gewonnen");
                    deactivateMask();
                    buttonSeeShips1.setVisible(false);
                    buttonSeeShips2.setVisible(false);
                    buttonReset.setVisible(false);
                    battleshipContainer.getChildren().add(wonleft);
                    wonleft.setX(50);
                    wonleft.setY(520);
                    winnerplay.stop();
                    winnerplay.play();
                    battleshipContainer.getChildren().add(buttonContinue);
                    buttonContinue.setLayoutX(160);
                    buttonContinue.setLayoutY(850);
                    buttonContinue.setVisible(true);
                }

            } else {
                a = calculateXY(x, y, 440 + 40 + 10 * 40 + 2 * 40, 40 + 40, 440 + 440 + 440 + 40, 440 + 40);
                if (a != null) {
                    if (player2.isAttackPossible(new Point(a[0], a[1]))) {
                        if (player1.playfield.attack(a[0], a[1])) {

                            drawAttack(a[0], a[1], x, y, player1);
                            player2.saveAttack(new Point(a[0], a[1]));
                            activateMask();
                            bombplay.stop();
                            bombplay.play();

                        } else {
                            drawMiss(x, y);
                            player2.saveAttack(new Point(a[0], a[1]));
                            activateMask();
                            indicate1.setVisible(true);
                            indicate2.setVisible(false);
                            missplay.stop();
                            missplay.play();
                        }

                    }
                }
                if (player1.playfield.gameOver()) {
                    logger.info("Spieler 2 hat gewonnen");
                    deactivateMask();
                    buttonSeeShips1.setVisible(false);
                    buttonSeeShips2.setVisible(false);
                    buttonReset.setVisible(false);
                    battleshipContainer.getChildren().add(wonright);
                    wonright.setX(1450);
                    wonright.setY(520);
                    winnerplay.stop();
                    winnerplay.play();
                    battleshipContainer.getChildren().add(buttonContinue);
                    buttonContinue.setLayoutX(1520);
                    buttonContinue.setLayoutY(850);
                    buttonContinue.setVisible(true);

                }
            }
        }
    }

    /*Wasserzeichen, gerundet auf die richtige Stelle setzen*/
    private void drawMiss(double x, double y) {
        int diffx = (int) x % 40;
        x -= diffx;

        int diffy = (int) y % 40;
        y -= diffy;
        ImageView miss = new ImageView("file:res/Waterhitmarker.png");
        miss.setX(x);
        miss.setY(y);
        battleshipContainer.getChildren().add(miss);
        gameRound++;

    }

    /*Feuerzeichen, gerundet auf die richtige Stelle. Wenn Schiff zerstört, richtiges destroyed Schiff setzen*/
    private void drawAttack(int xx, int yy, double xreal, double yreal, Player player) {
        ImageShip imageShipl;

        int diffx = (int) xreal % 40;
        xreal -= diffx;

        int diffy = (int) yreal % 40;
        yreal -= diffy;

        ImageView hit = new ImageView("file:res/Hit.png");
        hit.setX(xreal);
        hit.setY(yreal);
        battleshipContainer.getChildren().addAll(hit);


        Image image = new Image("file:res/1x2_Ship_Destroyed.png");
        /*Objekt ship wird entweder null oder ein Schiff zugewiesen (Siehe Klasse Ship, Methode isDestroyed). Wenn
        das Schiff zerstört ist, wird im switch case gefragt welche Länge und dementsprechen setzen wir das Schiff*/
        Ship ship = player.playfield.isDestroyed(xx, yy);

        if (ship != null) {
            logger.info("Schiff zerstört");
            switch (ship.getLength()) {
                case 0:
                    break;
                case 2:
                    image = new Image("file:res/1x2_Ship_Destroyed.png");
                    break;
                case 3:
                    image = new Image("file:res/1x3_Ship_Destroyed.png");
                    break;
                case 4:
                    image = new Image("file:res/1x4_Ship_Destroyed.png");
                    break;
                case 5:
                    image = new Image("file:res/1x5_Ship_Destroyed.png");
                    break;
            }

            int x;
            int y;
            //*40 um auf unsere Spielfeldkoordinaten zu kommen
            x = ship.getX() * 40;
            y = ship.getY() * 40;
            //Wird immer in das gegenüberliegende Feld gesetzt, deshalb stehen hier die Koordinaten vom Spieler 2
            if (player == player1) {
                x += 2 * 440 + 40 + 40;
                y += 2 * 40;

            } else {
                x += (440 + 40);
                y += (2 * 40);


            }

            /*Schiff kreiert und zum Battleshipcontainer dazugehaut und lock==true, um es nicht bewegbar zu machen*/
            imageShipl = new ImageShip(x - ship.getDivx(), y - ship.getDivy(), ship.getLength(), image);
            battleshipContainer.getChildren().add(imageShipl.getImageView());
            imageShipl.rotateTo(ship.getDirection());
            imageShipl.lock();
        }
    }

    //Alle Schiffe beider Spieler sind gesetzt, dann true
    private void shipsComplete() {
        if (player1.playfield.isFleetComplete() && player2.playfield.isFleetComplete()) {
            this.shipsComplete = true;
        }

    }

    //Für einzelne Methoden, siehe entsprechende Klassen. Canvas wird zurückgesetzt
    private void reset() {

        for (int i = 0; i < imageShip0.length; i++) {
            imageShip1[i].rotateTo(Direction.RIGHT);
            imageShip0[i].rotateTo(Direction.RIGHT);
            imageShip0[i].reset();
            imageShip1[i].reset();

        }
        player1.playfield.removeAll();
        player2.playfield.removeAll();
        player1.deleteAllAttacks();
        player2.deleteAllAttacks();
        gameRound = 1;
        shipsComplete = false;
        buttonSaveShipsRight.setVisible(true);
        buttonSaveShipsLeft.setVisible(true);
        battleshipContainer = new Pane();
        BackgroundImage background = new BackgroundImage(new Image("file:res/BattleshipsBackground.png", 1800, 1000,
                true, true), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);

        battleshipContainer.setBackground(new Background(background));
        drawGUI();
        battleshipContainer.getChildren().add(buttonReset);
        buttonReset.setVisible(true);
        startmenu.setVisible(false);
    }


=======
>>>>>>> ciu_branch
}