package org.usfirst.frc.team1360.robot.util;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.*;
import org.opencv.videoio.*;


import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.opencv.imgcodecs.*;
import org.opencv.imgproc.*;

public class OrbitVision 
{
	CvSink src;
	private static CvSource outputStream;
	private static CvSource bwStream;
	private Mat frame = new Mat();
	private Mat dst = new Mat();
	
	
	
	private List<MatOfPoint> contours1 = new ArrayList<MatOfPoint>();
	private List<MatOfPoint> contours2 = new ArrayList<MatOfPoint>();
	private List<MatOfPoint2f> contours2f = new ArrayList<MatOfPoint2f>();
	private Mat mHierarchy = new Mat();
	private Mat otherMat = new Mat(); 
	private double offset = 3000;
	private double offset2 = 3000;
	private int index = 0;
	private int index2 = 0;

	
	//HSV values
	private double HSub = 110; //110
	private double HAdd = 255; //255
	private double SSub = 200; //190
	private double SAdd = 255; //255
	private double VSub = 0; //0
	private double VAdd = 20; //25
	
	public OrbitVision()
	{
		CameraServer.getInstance().addAxisCamera("10.13.60.3");
		outputStream = CameraServer.getInstance().putVideo("Final Output", 320, 240);
		bwStream = CameraServer.getInstance().putVideo("Filtered Stream", 320, 240);
	}

	
	public void Calculate()
	{
		
			contours2.clear();
			contours2f.clear();
		
			src = CameraServer.getInstance().getVideo();
			

			 Scalar hsvLow = new Scalar(HSub, SSub, VSub);
			 Scalar hsvHigh = new Scalar(HAdd, SAdd, VAdd);
			
			src.grabFrame(frame);
			
			Core.inRange(frame, hsvLow, hsvHigh, dst);
			
			dst.copyTo(otherMat);
			
			Imgproc.findContours(otherMat, contours2, mHierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
			
			
			for(int i = 0; i < contours2.size(); i++)
			{
				double width = Imgproc.boundingRect(contours2.get(i)).width;
				double height = Imgproc.boundingRect(contours2.get(i)).height;
				
				if(width * height < 2822.0)
				{
					System.out.println(width*height);
					contours2.remove(i);
					
				}
			}
			
			
			for(int i = 0; i < contours2.size(); i++)
			{
				double width = Imgproc.boundingRect(contours2.get(i)).width;
				double height = Imgproc.boundingRect(contours2.get(i)).height;
				
				if((Math.abs(width / height) - 2.5) < offset)
				{
					index = i;
				
					offset = height / width;
				}
				
				if((Math.abs(width / height) - 2.5) < offset && i != index)
				{
					index2 = i;
					offset2 = height / width;
				}
				
			}
			
			if (index > -1 && contours2.size() > 0)
			{
				int x = Imgproc.boundingRect(contours2.get(index)).x;
				int y = Imgproc.boundingRect(contours2.get(index)).y;
				
				int width = Imgproc.boundingRect(contours2.get(index)).width;
				int height = Imgproc.boundingRect(contours2.get(index)).height;
				
				int x2 = Imgproc.boundingRect(contours2.get(index2)).x;
				int y2 = Imgproc.boundingRect(contours2.get(index2)).y;
				
				int width2 = Imgproc.boundingRect(contours2.get(index2)).width;
				int height2 = Imgproc.boundingRect(contours2.get(index2)).height;
				
				
				Imgproc.rectangle(frame, new Point(x, y), new Point(x + width, y + height), new Scalar(236, 68.05, 43.73), 5);
				Imgproc.rectangle(frame, new Point(x2, y2), new Point(x2 + width2, y2 + height2), new Scalar(236, 68.05, 43.73), 5);
				
				for(MatOfPoint point : contours2)
				{
					MatOfPoint2f newPoint = new MatOfPoint2f(point.toArray());
					contours2f.add(newPoint);
				}
				
				RotatedRect rect = Imgproc.minAreaRect(contours2f.get(index));
				RotatedRect rect2 = Imgproc.minAreaRect(contours2f.get(index2));
				
				Point[] vtx = new Point[4];
				Point[] vtx2 = new Point[4];
				
				rect.points(vtx);
				rect2.points(vtx2);
				
				for (int i = 0; i < 4; i++)
				{
					Imgproc.line(frame, vtx[i], vtx[(i+1)%4], new Scalar(0, 128, 235), 3);
					Imgproc.line(frame, vtx2[i], vtx2[(i+1)%4], new Scalar(0, 128, 235), 3);;
				}
				
				double dist1 = vtx[0].x - vtx[1].x;
				double dist2 = vtx[0].y - vtx[1].y;
				double angle = Math.toDegrees(Math.atan(dist2 / dist1));
				
				System.out.println(angle);
			}
			
			
			//System.out.println(index);			
			outputStream.putFrame(frame);
			bwStream.putFrame(dst);
	}
	
	
	
}
