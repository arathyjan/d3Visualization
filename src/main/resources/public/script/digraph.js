
function drawSvg() {

}

function drawForceGraph(startNodeName, dataset, svg) {
    var g = new dagreD3.Digraph();
// States and transitions from RFC 793
    var states = [ "CLOSED", "LISTEN", "SYN RCVD", "SYN SENT",
        "ESTAB", "FINWAIT-1", "CLOSE WAIT", "FINWAIT-2",
        "CLOSING", "LAST-ACK", "TIME WAIT" ];

// Automatically label each of the nodes
    states.forEach(function(state) { g.addNode(state, { label: state }); });

// Add some custom colors based on state
    g.node('CLOSED').style = 'fill: #f77';
    g.node('ESTAB').style  = 'fill: #7f7';

// Set up the edges
    g.addEdge(null, "CLOSED",     "LISTEN",     { label: "open" });
    g.addEdge(null, "LISTEN",     "SYN RCVD",   { label: "rcv SYN" });
    g.addEdge(null, "LISTEN",     "SYN SENT",   { label: "send" });
    g.addEdge(null, "LISTEN",     "CLOSED",     { label: "close" });
    g.addEdge(null, "SYN RCVD",   "FINWAIT-1",  { label: "close" });
    g.addEdge(null, "SYN RCVD",   "ESTAB",      { label: "rcv ACK of SYN" });
    g.addEdge(null, "SYN SENT",   "SYN RCVD",   { label: "rcv SYN" });
    g.addEdge(null, "SYN SENT",   "ESTAB",      { label: "rcv SYN, ACK" });
    g.addEdge(null, "SYN SENT",   "CLOSED",     { label: "close" });
    g.addEdge(null, "ESTAB",      "FINWAIT-1",  { label: "close" });
    g.addEdge(null, "ESTAB",      "CLOSE WAIT", { label: "rcv FIN" });
    g.addEdge(null, "FINWAIT-1",  "FINWAIT-2",  { label: "rcv ACK of FIN" });
    g.addEdge(null, "FINWAIT-1",  "CLOSING",    { label: "rcv FIN" });
    g.addEdge(null, "CLOSE WAIT", "LAST-ACK",   { label: "close" });
    g.addEdge(null, "FINWAIT-2",  "TIME WAIT",  { label: "rcv FIN" });
    g.addEdge(null, "CLOSING",    "TIME WAIT",  { label: "rcv ACK of FIN" });
    g.addEdge(null, "LAST-ACK",   "CLOSED",     { label: "rcv ACK of FIN" });
    g.addEdge(null, "TIME WAIT",  "CLOSED",     { label: "timeout=2MSL" });

// Create the renderer
    var renderer = new dagreD3.Renderer();

// Set up an SVG group so that we can translate the final graph.
    var svg = d3.select('svg'),
        svgGroup = svg.append('g');

// Set initial zoom to 75%
    var initialScale = 0.75;
    var oldZoom = renderer.zoom();
    renderer.zoom(function(graph, svg) {
        var zoom = oldZoom(graph, svg);

        // We must set the zoom and then trigger the zoom event to synchronize
        // D3 and the DOM.
        zoom.scale(initialScale).event(svg);
        return zoom;
    });

// Run the renderer. This is what draws the final graph.
    var layout = renderer.run(g, svgGroup);

// Center the graph
    var xCenterOffset = (svg.attr('width') - layout.graph().width * initialScale) / 2;
    svgGroup.attr('transform', 'translate(' + xCenterOffset + ', 20)');
    svg.attr('height', layout.graph().height * initialScale + 40);}
}