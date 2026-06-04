#!/bin/bash

echo "Cleaning old build files..."
rm -f *.class
rm -rf dummy
mkdir dummy

echo "Compiling the emergency room package..."
javac -d . Doctor.java DoctorQueue.java Patient.java PatientHistoryBST.java PatientLookup.java SeverityLevel.java

if [ $? -ne 0 ]; then
    echo ""
    echo "Error during package compilation."
    exit 1
fi

echo "Compiling the triage system..."
javac -sourcepath dummy -cp . TriageSystem.java

if [ $? -ne 0 ]; then
    echo ""
    echo "Error during triage system compilation."
    exit 1
fi

echo "Compilation successful! Launching the application..."
echo ""
java -cp . TriageSystem
