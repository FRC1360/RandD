
package org.usfirst.frc.team1360.robot;

import org.usfirst.frc.team1360.auto.AutonControl;
import org.usfirst.frc.team1360.robot.IO.HumanInput;
import org.usfirst.frc.team1360.robot.IO.RobotOutput;
import org.usfirst.frc.team1360.robot.IO.SensorInput;
import org.usfirst.frc.team1360.robot.teleop.TeleopControl;
import org.usfirst.frc.team1360.robot.util.OrbitVision;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

	private RobotOutput robotOutput;
	private HumanInput humanInput;
	private SensorInput sensorInput;
	private TeleopControl teleopControl;
	private OrbitVision orbitVision;

	
    public void robotInit() 
    {
    	this.robotOutput = RobotOutput.getInstance();
    	this.humanInput = HumanInput.getInstance();
    	this.teleopControl = TeleopControl.getInstance();
    	this.sensorInput = SensorInput.getInstance();
    	this.orbitVision = new OrbitVision();
    }
    

    public void autonomousInit() 
    {
    	AutonControl.getInstance().initialize();
    
    }

    public void disabledInit()
    {
    	this.robotOutput.stopAll();
    	this.teleopControl.disable();
    }
    
    public void disabledPeriodic()
    {
    	this.sensorInput.calculate();
    	AutonControl.getInstance().updateModes();

    }

    public void autonomousPeriodic()
    {
    	orbitVision.Calculate();
    	//AutonControl.getInstance().runCycle();
    }


    public void teleopPeriodic()
    {
        this.sensorInput.calculate();
        this.teleopControl.runCycle();
    }
 
}
