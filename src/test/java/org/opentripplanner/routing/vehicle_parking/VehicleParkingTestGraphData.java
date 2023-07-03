package org.opentripplanner.routing.vehicle_parking;

import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.street.model.StreetTraversalPermission;
import org.opentripplanner.street.model.vertex.IntersectionVertex;
import org.opentripplanner.street.model.vertex.VertexFactory;
import org.opentripplanner.transit.model.framework.Deduplicator;
import org.opentripplanner.transit.service.StopModel;
import org.opentripplanner.transit.service.TransitModel;

public class VehicleParkingTestGraphData {

  protected IntersectionVertex A, B;

  protected Graph graph;

  protected TransitModel transitModel;

  public void initGraph() {
    var deduplicator = new Deduplicator();
    var stopModel = new StopModel();
    graph = new Graph(deduplicator);
    transitModel = new TransitModel(stopModel, deduplicator);
    graph.hasStreets = true;

    var intersection = new VertexFactory(graph);

    A = intersection.intersection("A", 0, 0);
    B = intersection.intersection("B", 0.01, 0);

    VehicleParkingTestUtil.createStreet(A, B, StreetTraversalPermission.PEDESTRIAN);
  }

  public Graph getGraph() {
    return graph;
  }

  public TransitModel getTransitModel() {
    return transitModel;
  }

  public IntersectionVertex getAVertex() {
    return A;
  }

  public IntersectionVertex getBVertex() {
    return B;
  }
}
