Array.prototype.minus = function(subArray){
    return this.filter(function(element){
        return subArray.indexOf(element) === -1;

    });
};

Array.prototype.removeDuplicates = function(){
    var filteredArray = [];
    this.forEach(function(element){
        var tempArr = filteredArray.filter(function(el){
            return el === element;
        });
        if(tempArr.length === 0) {
            filteredArray.push(element);
        }
    });
    return filteredArray;
};

var w = window,
   d = document,
   e = d.documentElement,
   g = d.getElementsByTagName('body')[0],
   windowWidth = w.innerWidth || e.clientWidth || g.clientWidth,
   windowHeight = w.innerHeight|| e.clientHeight|| g.clientHeight;


var colors = d3.scale.category10();

var linkDistance = 150;
var margin = {top: -5, right: -5, bottom: -5, left: -5},
    width = windowWidth - 25,
    height = windowHeight - 50;

function drawSvg() {
    var zoom = d3.behavior.zoom()
            .scaleExtent([0, 10])
            .on("zoom", zoomed);

    function zoomed() {
        svg.attr("transform", "scale(" + d3.event.scale + ")");
        d3.select("svg").attr({"height": height * d3.event.scale,"width": width * d3.event.scale});
    }

    var svg = d3.select("body")
            .append("div")
            .attr("id","container")
            .style({"max-height": (windowHeight - 50) + 'px',"max-width": (windowWidth - 25) + 'px',"min-height": (windowHeight - 50) + 'px'})
            .style({"overflow" : "scroll"})
            .append("svg")
            .attr({"height": height,"width": width})
            .attr('id', 'svg_id')
            .call(zoom)
            .append("g");

    return svg;
}

function drawForceGraph(startNodeName, dataset, svg) {
    if (dataset.nodes.length === 0) {
        svg.remove();
        document.getElementById('error_message').innerHTML = 'No transitions start from the selected state.';
    }
    else {
        document.getElementById('error_message').innerHTML = '';
        var i = 0;
        dataset.edges.forEach(function (edge) {
            edge.index = i;
            i++;
        });


        var force = d3.layout.force()
                .nodes(dataset.nodes)
                .links(dataset.edges)
                .size([width, height])
                .linkDistance([linkDistance])
                .charge([-400])
                .theta(0.1)
                .gravity(0)
                .friction(0)
                .start();

        var drag = force.drag()
            .on("dragstart", function(d) {
                d.fixed = true;
            });


        // Show collapsed graph with only starting node
        collapseAllNodes(startNodeName);
        collapseAllEdges();

        var edges = svg.selectAll("line")
                .data(dataset.edges)
                .enter()
                .append("line")
                .attr("id", function (d, i) {
                    return 'edge' + i
                })
                .attr('marker-end', 'url(#arrowhead)')
                .attr('visibility', function (d) {
                    return d.visibility == undefined || d.visibility === true ? 'visible' : 'hidden'
                })
                .style("stroke", "#ccc")
                .style("pointer-events", "none");


        var nodes = svg.selectAll("circle")
                .data(dataset.nodes)
                .enter()
                .append("circle")
                .attr({"r": 15})
                .attr('visibility', function (d) {
                    return d.visibility == undefined || d.visibility === true ? 'visible' : 'hidden'
                })
                .style("fill", function (d, i) {
                    return colors(i);
                })
                .on("click", click)
                .call(drag);

        var nodelabels = svg.selectAll(".nodelabel")
                .data(dataset.nodes)
                .enter()
                .append("text")
                .attr({"x": function (d) {
                    return d.x;
                },
                    "y": function (d) {
                        return d.y;
                    },
                    "class": "nodelabel",
                    "stroke": "black"})
                .attr('visibility', function (d) {
                    return d.visibility == undefined || d.visibility === true ? 'visible' : 'hidden'
                })
                .text(function (d) {
                    return d.label;
                });

        var edgepaths = svg.selectAll(".edgepath")
                .data(dataset.edges)
                .enter()
                .append('path')
                .attr({'d': function (d) {
                    return 'M ' + d.source.x + ' ' + d.source.y + ' L ' + d.target.x + ' ' + d.target.y
                },
                    'class': 'edgepath',
                    'fill-opacity': 0,
                    'stroke-opacity': 0,
                    'fill': 'blue',
                    'stroke': 'red',
                    'id': function (d, i) {
                        return 'edgepath' + i
                    }
                })
                .attr('visibility', function (d) {
                    return d.visibility == undefined || d.visibility === true ? 'visible' : 'hidden'
                })
                .style("pointer-events", "none");

        var edgelabels = svg.selectAll(".edgelabel")
                .data(dataset.edges)
                .enter()
                .append('text')
                .style("pointer-events", "none")
                .attr({'class': 'edgelabel',
                    'id': function (d, i) {
                        return 'edgelabel' + i
                    },
                    'dx': 30,
                    'dy': -5,
                    'font-size': 10,
                    'fill': '#aaa'})
                .attr('visibility', function (d) {
                    return d.visibility == undefined || d.visibility === true ? 'visible' : 'hidden'
                });

        var edgetextpath = edgelabels.append('textPath')
                .attr('xlink:href', function (d, i) {
                    return '#edgepath' + i
                })
                .attr('visibility', function (d) {
                    return d.visibility == undefined || d.visibility === true ? 'visible' : 'hidden'
                })
                .style("pointer-events", "none")
                .text(function (d, i) {
                    return d.condition
                });

        svg.append('defs').append('marker')
                .attr({'id': 'arrowhead',
                    'viewBox': '-0 -5 10 10',
                    'refX': 25,
                    'refY': 0,
                    'orient': 'auto',
                    'markerWidth': 10,
                    'markerHeight': 10,
                    'xoverflow': 'visible'})
                .append('svg:path')
                .attr('d', 'M 0,-5 L 10 ,0 L 0,5')
                .attr('fill', '#ccc')
                .attr('stroke', '#ccc');

        force.on("tick", function () {

            nodes.attr({"cx": function (d) {
                return d.x = Math.max(15, Math.min(width - 15, d.x));
            }, "cy": function (d) {
                return d.y = Math.max(15, Math.min(height - 15, d.y));
            }});

            nodelabels.attr("x", function (d) {
                            return Math.max(15, Math.min(width - d.label.length * 6.6 - 15, d.x + 14));
                       })
                       .attr("y", function (d) {
                            return Math.max(15, Math.min(height - 15, d.y));
                        });


            edges.attr({"x1": function (d) {
                return d.source.x;
            },
                "y1": function (d) {
                    return d.source.y;
                },
                "x2": function (d) {
                    return d.target.x;
                },
                "y2": function (d) {
                    return d.target.y;
                }
            });

            edgepaths.attr('d', function (d) {
                return 'M ' + d.source.x + ' ' + d.source.y + ' L ' + d.target.x + ' ' + d.target.y;
            });

        });



        function collapseAllNodes(startNodeName) {
            dataset.nodes.forEach(function (node) {
                dataset.nodes[node.index].visibility = node.label === startNodeName;
            })
        }

        function collapseAllEdges() {
            dataset.edges.forEach(function (edge) {
                dataset.edges[edge.index].visibility = false;
            })
        }

        function expandNextLevelTree(d) {
            var edgesFromClickedNode = dataset.edges.filter(function (edge) {
                return edge.source.index === d.index;

            });
            var nextLevelNodes = [];
            edgesFromClickedNode.forEach(function (edge) {
                nextLevelNodes.push(edge.target);
            });

            nextLevelNodes.forEach(function (node) {
                dataset.nodes[node.index].visibility = true;
            });

            edgesFromClickedNode.forEach(function (edge) {
                dataset.edges[edge.index].visibility = true;
            });
        }

        function collapseTree(d) {
            var visitedNodeIndices = [];
            var edgesToHide = findEdgesRecursively(dataset.edges, d.index).removeDuplicates();
            var edgesToRetain = dataset.edges.minus(edgesToHide);

            var nodesToRetain = [];
            edgesToRetain.forEach(function (edge) {
                if (edge.visibility == true) {
                    nodesToRetain.push(edge.source);
                    nodesToRetain.push(edge.target);
                }
            });
            nodesToRetain = nodesToRetain.removeDuplicates();

            var nodesToHide = dataset.nodes.minus(nodesToRetain);

            edgesToHide.forEach(function (edge) {
                dataset.edges[edge.index].visibility = false;
            });

            nodesToHide.forEach(function (node) {
                dataset.nodes[node.index].visibility = false;
            });

            dataset.nodes[d.index].visibility = true;

            function findEdgesRecursively(edges, sourceNodeId) {
                if (visitedNodeIndices.indexOf(sourceNodeId) !== -1)
                    return [];

                visitedNodeIndices.push(sourceNodeId);
                var outgoingEdgesFromSource = edges.filter(function (edge) {
                    return edge.source.index === sourceNodeId && edge.visibility === true;
                });

                var allEdgesonPath = [];
                allEdgesonPath = allEdgesonPath.concat(outgoingEdgesFromSource);

                // Find all edges that are part of the tree traversal starting at the Source Node.
                outgoingEdgesFromSource.forEach(function (edge) {
                    allEdgesonPath = allEdgesonPath.concat(findEdgesRecursively(edges, edge.target.index));
                });
                return allEdgesonPath;
            }
        }

        function click(d) {

            if (d3.event.defaultPrevented) return; // ignore drag

            dataset.nodes[d.index].clicked = dataset.nodes[d.index].clicked == undefined ? true : !dataset.nodes[d.index].clicked;
            var visibility = dataset.nodes[d.index].clicked;

            if (visibility == true) expandNextLevelTree(d);
            else collapseTree(d);

            nodes.style('visibility', function (d) {
                return d.visibility == undefined || d.visibility === true ? 'visible' : 'hidden'
            });
            nodelabels.style('visibility', function (d) {
                return d.visibility == undefined || d.visibility === true ? 'visible' : 'hidden'
            });
            edges.style('visibility', function (d) {
                return d.visibility == undefined || d.visibility === true ? 'visible' : 'hidden'
            });
            edgepaths.style('visibility', function (d) {
                return d.visibility == undefined || d.visibility === true ? 'visible' : 'hidden'
            });
            edgelabels.style('visibility', function (d) {
                return d.visibility == undefined || d.visibility === true ? 'visible' : 'hidden'
            });
            edgetextpath.style('visibility', function (d) {
                return d.visibility == undefined || d.visibility === true ? 'visible' : 'hidden'
            })


        }
    }
}