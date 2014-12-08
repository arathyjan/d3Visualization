package com.barclays.ivr.viz.controllers;

import com.barclays.ivr.viz.domain.Graph;
import com.barclays.ivr.viz.domain.Visual;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class VisualisationController {

    public static final Visual VISUAL = new Visual("/vui.xml");

    @RequestMapping(value="/stateflow",method = RequestMethod.GET)
    public String representStateFlows(Model model) throws Exception {
        final Graph graph = VISUAL.draw();
        model.addAttribute("stateOptions", graph.nodes);
        return "ivr_viz";
    }

    @RequestMapping(value="/plotState",method = RequestMethod.GET)
    public ResponseEntity<Graph> representFlowFor(@RequestParam(value = "state",defaultValue = "All",required = true) String state)throws Exception {
        final Graph graph = state.equals("All")
                ? VISUAL.draw() : VISUAL.drawRecursively(state);

        return new ResponseEntity<Graph>(graph, HttpStatus.OK);
    }

}
