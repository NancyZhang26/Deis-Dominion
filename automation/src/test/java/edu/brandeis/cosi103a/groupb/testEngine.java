package edu.brandeis.cosi103a.groupb;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue; //Use java reflection to test private method
import org.junit.jupiter.api.Test;

import edu.brandeis.cosi103a.groupb.Cards.Card;
import edu.brandeis.cosi103a.groupb.Decks.GameDeck;
import edu.brandeis.cosi103a.groupb.Decks.PlayerDeck;
import edu.brandeis.cosi103a.groupb.Game.ConsoleGameObserver;
import edu.brandeis.cosi103a.groupb.Game.GameEngine;
import edu.brandeis.cosi103a.groupb.Game.GameObserver;
import edu.brandeis.cosi103a.groupb.Player.BigMoneyPlayer;
import edu.brandeis.cosi103a.groupb.Player.HumanPlayer;
import edu.brandeis.cosi103a.groupb.Player.Player;

public class testEngine {
    private Player player1 = new BigMoneyPlayer("Nancy");
    private Player player2 = new HumanPlayer("Abby");
    private GameObserver observer = new ConsoleGameObserver();
    private GameEngine gameEngine = new GameEngine(player1, player2, observer);

    @Test
    void testInitializeDeck() throws Exception {
        Method initializeDeck = gameEngine.getClass().getDeclaredMethod("initializeDeck");
        initializeDeck.setAccessible(true);
        GameDeck deck = (GameDeck) initializeDeck.invoke(gameEngine);

        assertEquals(60, deck.getNumAvailable(Card.Type.BITCOIN));
        assertEquals(40, deck.getNumAvailable(Card.Type.ETHEREUM));
        assertEquals(30, deck.getNumAvailable(Card.Type.DOGECOIN));
        assertEquals(14, deck.getNumAvailable(Card.Type.METHOD));
        assertEquals(8, deck.getNumAvailable(Card.Type.MODULE));
        assertEquals(8, deck.getNumAvailable(Card.Type.FRAMEWORK));
    }

    @Test
    void testInitializeGameState() throws Exception {
        Method initializeGameState = gameEngine.getClass().getDeclaredMethod("initializeGameState", Player.class);
        initializeGameState.setAccessible(true);
        initializeGameState.invoke(gameEngine, player1);
        initializeGameState.invoke(gameEngine, player2);

        PlayerDeck discardDeck = player1.getDiscardDeck();
        PlayerDeck drawDeck = player2.getDrawDeck();

        assertTrue(discardDeck.size() == 0);
        assertTrue(drawDeck.size() == 10);
    }

    // Only testing Big Money Player here
    @Test
    void testPhases() throws Exception {
        Field mainDeck = gameEngine.getClass().getDeclaredField("deck"); 
        mainDeck.setAccessible(true);

        Method initializeGameState = gameEngine.getClass().getDeclaredMethod("initializeGameState", Player.class);
        initializeGameState.setAccessible(true);
        initializeGameState.invoke(gameEngine, player1);

        Method moneyPhase = gameEngine.getClass().getDeclaredMethod("handleMoneyPhase", Player.class);
        moneyPhase.setAccessible(true);
        moneyPhase.invoke(gameEngine, player1);

        assertTrue(player1.getDrawDeck().size() == 5);

        Method buyPhase = gameEngine.getClass().getDeclaredMethod("handleBuyPhase", Player.class);
        buyPhase.setAccessible(true);
        buyPhase.invoke(gameEngine, player1);

        assertEquals(player1.getDiscardDeck().size(), 1); //Bought card directly goes into the discard deck.

        Method cleanUpPhase = gameEngine.getClass().getDeclaredMethod("handleCleanupPhase", Player.class);
        cleanUpPhase.setAccessible(true);
        cleanUpPhase.invoke(gameEngine, player1);

        assertEquals(player1.getDiscardDeck().size(), 6);
        assertEquals(player1.getDrawDeck().size(), 5);
        
    }
    

    
}
