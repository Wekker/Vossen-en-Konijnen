package controller;

import java.util.ConcurrentModificationException;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

import javax.swing.JTextField;

import view.Field;
import view.SimulatorView;

import logic.Location;
import logic.Randomizer;
import model.Actor;
import model.Animal;
import model.Bear;
import model.Fox;
import model.Grass;
import model.Hunter;
import model.Rabbit;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 * 
 * @author Ieme, Jermo, Yisong
 * @version 2012.01.29
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a bear will be created in any given grid position.    
    private static final double BEAR_CREATION_PROBABILITY = 0.015;
    // The probability that a fox will be created in any given grid position.
    private static final double FOX_CREATION_PROBABILITY = 0.02;
    // The probability that a rabbit will be created in any given grid position.
    private static final double RABBIT_CREATION_PROBABILITY = 0.08;    
    // The probability that a hunter will be created in any given grid position.
    private static final double HUNTER_CREATION_PROBABILITY = 0.0025;
    // the probability that grass will grow.
    private static final double GRASS_CREATION_PROBABILITY = 0.03;
    
    //	animation speed of the thread
    private static int animationSpeed = 1;//100;
    // List of Actors in the field.
    private List<Actor> actors;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step = 0;
    // A graphical view of the simulation.
    private SimulatorView view;

    
    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
//        thread	=	new Thread(this);
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
//            thread	=	new Thread(this);
        }
        
        actors = new ArrayList<Actor>();
        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Rabbit.class, Color.ORANGE);
        view.setColor(Fox.class, Color.BLUE);
        view.setColor(Bear.class, Color.CYAN);
        view.setColor(Hunter.class, Color.RED);
        view.setColor(Grass.class, Color.GREEN);
        // Setup a valid starting point.
        reset();
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * fox and rabbit.
     */
    public void simulateOneStep()
    {
        step++;

        // Provide space for newborn actors.
        List<Actor> newActors = new ArrayList<Actor>();
        
        // avoid being being interrupted
        try{
	        	if (actors != null)
	        	{
				    // Let all actors act.
				    for(Iterator<Actor> it = actors.iterator(); it.hasNext();) {
				        Actor actor = it.next();
				        actor.act(newActors);
				        if (actor instanceof Animal)		//	check if actor is an animal
				        {
				        	Animal animal = (Animal) actor;
				        	if(! animal.isAlive()) {
				                it.remove();
				            }
				        }
				    }
	        	}
        }
        catch (ConcurrentModificationException e)
        {
        	simulateOneStep();
        }
        // Add the newly born foxes and rabbits to the main lists.
        actors.addAll(newActors);

        view.showStatus(step, field);
    }
    
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
		view.getThreadRunner().stop();			
        step = 0;
        actors.clear();
        populate();
        
        // Show the starting state in the view.
        view.showStatus(step, field);
    }
    
    /**
     * Getter voor view
     * @return view van het type SimulatorView
     */
    public SimulatorView getSimulatorView()
    {
    	return view;
    }
    
    /**
     * Getter voor field
     * @return field van het type Field
     */
    public Field getField()
    {
    	return field;
    }
    
    /**
     * getter voor step
     * @return steps aantal step
     */
    public int getStep()
    {
    	return step;
    }
    
    /**
     * setter voor animationSpeed
     * @param animationSpeed2
     */
    public static void setAnimationSpeed(int animationSpeed)
    {
    	Simulator.animationSpeed = animationSpeed;
    }
    
    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= FOX_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Fox fox = new Fox(true, field, location);
                    actors.add(fox);
                }
                if(rand.nextDouble() <= RABBIT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Rabbit rabbit = new Rabbit(true, field, location);
                    actors.add(rabbit);
                }
                if(rand.nextDouble() <= BEAR_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Bear bear = new Bear(true, field, location);
                    actors.add(bear);
                }
                if(rand.nextDouble() <= HUNTER_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Hunter hunter = new Hunter(field, location);
                    actors.add(hunter);
                }
                if(rand.nextDouble() <= GRASS_CREATION_PROBABILITY) {
                	Location location = new Location(row, col);
                	Grass grass = new Grass(true, field, location);
                	actors.add(grass);
                }
                // else leave the location empty.
            }
        }
    }
}