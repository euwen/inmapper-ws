package com.inmapper.ws.evaluation;

import com.inmapper.ws.evaluation.components.DataAnalysis;
import com.inmapper.ws.evaluation.components.Plotter;
import com.inmapper.ws.evaluation.components.SessionAuditor;

public class Evaluations {
    
    private final DataAnalysis analysis = new DataAnalysis(new SessionAuditor(), new Plotter());
    
    public static void main(String[] args) {
        new Evaluations().start();
    }
    
    private void start() {
        // trein's 60 step mapping test
        // this.analysis.analyze("ios-mapping",
        // "1384645335327-9d21f629-9555-4071-ac22-68176db4c5ab");
        
        // navjot's 18 step test
        // this.analysis.analyze("ios-steps", "1384645705006-ed232730-16c0-4777-9b72-c50601dbac80");
        
        // navjot's 19 step test
        // this.analysis.analyze("ios-steps", "1384645859833-c1c734f0-2eb1-47b1-af20-ff0a3cc2a9ed");
        
        // navjot's 20 step test
        // this.analysis.analyze("ios-steps", "1384642987877-432233f4-1cff-4e5e-a675-6737b1b464ba");
        
        this.analysis.analyze("ios-mapping-trein", "1386789017664-fc500eb7-2c36-4d80-869d-c39f53f3261a");
    }
}
