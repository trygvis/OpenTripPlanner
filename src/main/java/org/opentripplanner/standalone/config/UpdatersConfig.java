package org.opentripplanner.standalone.config;

import static org.opentripplanner.standalone.config.UpdatersConfig.Type.BIKE_RENTAL;
import static org.opentripplanner.standalone.config.UpdatersConfig.Type.MQTT_GTFS_RT_UPDATER;
import static org.opentripplanner.standalone.config.UpdatersConfig.Type.REAL_TIME_ALERTS;
import static org.opentripplanner.standalone.config.UpdatersConfig.Type.SIRI_AZURE_ET_UPDATER;
import static org.opentripplanner.standalone.config.UpdatersConfig.Type.SIRI_AZURE_SX_UPDATER;
import static org.opentripplanner.standalone.config.UpdatersConfig.Type.SIRI_ET_GOOGLE_PUBSUB_UPDATER;
import static org.opentripplanner.standalone.config.UpdatersConfig.Type.SIRI_ET_UPDATER;
import static org.opentripplanner.standalone.config.UpdatersConfig.Type.SIRI_SX_UPDATER;
import static org.opentripplanner.standalone.config.UpdatersConfig.Type.SIRI_VM_UPDATER;
import static org.opentripplanner.standalone.config.UpdatersConfig.Type.STOP_TIME_UPDATER;
import static org.opentripplanner.standalone.config.UpdatersConfig.Type.VEHICLE_PARKING;
import static org.opentripplanner.standalone.config.UpdatersConfig.Type.VEHICLE_POSITIONS;
import static org.opentripplanner.standalone.config.UpdatersConfig.Type.VEHICLE_RENTAL;
import static org.opentripplanner.standalone.config.UpdatersConfig.Type.WEBSOCKET_GTFS_RT_UPDATER;
import static org.opentripplanner.standalone.config.UpdatersConfig.Type.WINKKI_POLLING_UPDATER;
import static org.opentripplanner.standalone.config.framework.json.OtpVersion.NA;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import org.opentripplanner.ext.siri.updater.SiriETGooglePubsubUpdaterParameters;
import org.opentripplanner.ext.siri.updater.SiriETUpdaterParameters;
import org.opentripplanner.ext.siri.updater.SiriSXUpdaterParameters;
import org.opentripplanner.ext.siri.updater.SiriVMUpdaterParameters;
import org.opentripplanner.ext.siri.updater.azure.SiriAzureETUpdaterParameters;
import org.opentripplanner.ext.siri.updater.azure.SiriAzureSXUpdaterParameters;
import org.opentripplanner.ext.vehiclerentalservicedirectory.VehicleRentalServiceDirectoryFetcher;
import org.opentripplanner.ext.vehiclerentalservicedirectory.api.VehicleRentalServiceDirectoryFetcherParameters;
import org.opentripplanner.standalone.config.framework.json.NodeAdapter;
import org.opentripplanner.standalone.config.sandbox.VehicleRentalServiceDirectoryFetcherConfig;
import org.opentripplanner.standalone.config.updaters.GtfsRealtimeAlertsUpdaterConfig;
import org.opentripplanner.standalone.config.updaters.MqttGtfsRealtimeUpdaterConfig;
import org.opentripplanner.standalone.config.updaters.PollingStoptimeUpdaterConfig;
import org.opentripplanner.standalone.config.updaters.SiriETGooglePubsubUpdaterConfig;
import org.opentripplanner.standalone.config.updaters.SiriETUpdaterConfig;
import org.opentripplanner.standalone.config.updaters.SiriSXUpdaterConfig;
import org.opentripplanner.standalone.config.updaters.SiriVMUpdaterConfig;
import org.opentripplanner.standalone.config.updaters.VehicleParkingUpdaterConfig;
import org.opentripplanner.standalone.config.updaters.VehiclePositionsUpdaterConfig;
import org.opentripplanner.standalone.config.updaters.VehicleRentalUpdaterConfig;
import org.opentripplanner.standalone.config.updaters.WFSNotePollingGraphUpdaterConfig;
import org.opentripplanner.standalone.config.updaters.WebsocketGtfsRealtimeUpdaterConfig;
import org.opentripplanner.standalone.config.updaters.azure.SiriAzureETUpdaterConfig;
import org.opentripplanner.standalone.config.updaters.azure.SiriAzureSXUpdaterConfig;
import org.opentripplanner.updater.TimetableSnapshotSourceParameters;
import org.opentripplanner.updater.UpdatersParameters;
import org.opentripplanner.updater.alert.GtfsRealtimeAlertsUpdaterParameters;
import org.opentripplanner.updater.street_note.WFSNotePollingGraphUpdaterParameters;
import org.opentripplanner.updater.trip.MqttGtfsRealtimeUpdaterParameters;
import org.opentripplanner.updater.trip.PollingTripUpdaterParameters;
import org.opentripplanner.updater.trip.WebsocketGtfsRealtimeUpdaterParameters;
import org.opentripplanner.updater.vehicle_parking.VehicleParkingUpdaterParameters;
import org.opentripplanner.updater.vehicle_position.VehiclePositionsUpdaterParameters;
import org.opentripplanner.updater.vehicle_rental.VehicleRentalUpdaterParameters;

/**
 * This class maps between the JSON array of updaters and the concrete class implementations of each
 * updater parameters. Some updaters use the same parameters, so a map is kept between the JSON
 * updater type strings and the appropriate updater parameter class.
 */
public class UpdatersConfig implements UpdatersParameters {

  private final Multimap<Type, Object> configList = ArrayListMultimap.create();

  private final TimetableSnapshotSourceParameters timetableUpdates;

  @Nullable
  private final VehicleRentalServiceDirectoryFetcherParameters vehicleRentalServiceDirectoryFetcherParameters;

  public UpdatersConfig(NodeAdapter rootAdapter) {
    this.vehicleRentalServiceDirectoryFetcherParameters =
      VehicleRentalServiceDirectoryFetcherConfig.create(
        rootAdapter.exist("vehicleRentalServiceDirectory")
          ? rootAdapter
            .of("vehicleRentalServiceDirectory")
            .withDoc(NA, /*TODO DOC*/"TODO")
            .withExample(/*TODO DOC*/"TODO")
            .withDescription(/*TODO DOC*/"TODO")
            .asObject()
          : rootAdapter
            .of("bikeRentalServiceDirectory")
            .withDoc(NA, /*TODO DOC*/"TODO")
            .withExample(/*TODO DOC*/"TODO")
            .withDescription(/*TODO DOC*/"TODO")
            .asObject() // TODO: deprecated, remove in next major version
      );

    timetableUpdates =
      timetableUpdates(
        rootAdapter
          .of("timetableUpdates")
          .withDoc(NA, /*TODO DOC*/"TODO")
          .withExample(/*TODO DOC*/"TODO")
          .withDescription(/*TODO DOC*/"TODO")
          .asObject()
      );

    List<NodeAdapter> updaters = rootAdapter
      .of("updaters")
      .withDoc(NA, /*TODO DOC*/"TODO")
      .withExample(/*TODO DOC*/"TODO")
      .withDescription(/*TODO DOC*/"TODO")
      .asObject()
      .asList();

    for (NodeAdapter conf : updaters) {
      Type type = conf
        .of("type")
        .withDoc(NA, /*TODO DOC*/"TODO")
        .withExample(/*TODO DOC*/"TODO")
        .asEnum(Type.class);

      configList.put(type, type.parseConfig(conf));
    }
  }

  /**
   * Read "timetableUpdates" parameters. These parameters are used to configure the
   * TimetableSnapshotSource. Both the GTFS and Siri version uses the same parameters.
   */
  private TimetableSnapshotSourceParameters timetableUpdates(NodeAdapter c) {
    var dflt = TimetableSnapshotSourceParameters.DEFAULT;
    if (c.isEmpty()) {
      return dflt;
    }

    // TODO DOC - Deprecate c.of("logFrequency").withDoc(NA, /*TODO DOC*/"TODO").asInt(dflt.logFrequency())

    return new TimetableSnapshotSourceParameters(
      c
        .of("maxSnapshotFrequency")
        .withDoc(NA, /*TODO DOC*/"TODO")
        .asInt(dflt.maxSnapshotFrequencyMs()),
      c.of("purgeExpiredData").withDoc(NA, /*TODO DOC*/"TODO").asBoolean(dflt.purgeExpiredData())
    );
  }

  public TimetableSnapshotSourceParameters timetableSnapshotParameters() {
    return timetableUpdates;
  }

  /**
   * This is the endpoint url used for the VehicleRentalServiceDirectory sandbox feature.
   *
   * @see VehicleRentalServiceDirectoryFetcher
   */
  @Override
  @Nullable
  public VehicleRentalServiceDirectoryFetcherParameters getVehicleRentalServiceDirectoryFetcherParameters() {
    return this.vehicleRentalServiceDirectoryFetcherParameters;
  }

  @Override
  public List<VehicleRentalUpdaterParameters> getVehicleRentalParameters() {
    ArrayList<VehicleRentalUpdaterParameters> result = new ArrayList<>(
      getParameters(VEHICLE_RENTAL)
    );
    result.addAll(getParameters(BIKE_RENTAL));
    return result;
  }

  @Override
  public List<GtfsRealtimeAlertsUpdaterParameters> getGtfsRealtimeAlertsUpdaterParameters() {
    return getParameters(REAL_TIME_ALERTS);
  }

  @Override
  public List<PollingTripUpdaterParameters> getPollingStoptimeUpdaterParameters() {
    return getParameters(STOP_TIME_UPDATER);
  }

  @Override
  public List<VehiclePositionsUpdaterParameters> getVehiclePositionsUpdaterParameters() {
    return getParameters(VEHICLE_POSITIONS);
  }

  @Override
  public List<SiriETUpdaterParameters> getSiriETUpdaterParameters() {
    return getParameters(SIRI_ET_UPDATER);
  }

  @Override
  public List<SiriETGooglePubsubUpdaterParameters> getSiriETGooglePubsubUpdaterParameters() {
    return getParameters(SIRI_ET_GOOGLE_PUBSUB_UPDATER);
  }

  @Override
  public List<SiriSXUpdaterParameters> getSiriSXUpdaterParameters() {
    return getParameters(SIRI_SX_UPDATER);
  }

  @Override
  public List<SiriVMUpdaterParameters> getSiriVMUpdaterParameters() {
    return getParameters(SIRI_VM_UPDATER);
  }

  @Override
  public List<WebsocketGtfsRealtimeUpdaterParameters> getWebsocketGtfsRealtimeUpdaterParameters() {
    return getParameters(WEBSOCKET_GTFS_RT_UPDATER);
  }

  @Override
  public List<MqttGtfsRealtimeUpdaterParameters> getMqttGtfsRealtimeUpdaterParameters() {
    return getParameters(MQTT_GTFS_RT_UPDATER);
  }

  @Override
  public List<VehicleParkingUpdaterParameters> getVehicleParkingUpdaterParameters() {
    return getParameters(VEHICLE_PARKING);
  }

  @Override
  public List<WFSNotePollingGraphUpdaterParameters> getWinkkiPollingGraphUpdaterParameters() {
    return getParameters(WINKKI_POLLING_UPDATER);
  }

  @Override
  public List<SiriAzureETUpdaterParameters> getSiriAzureETUpdaterParameters() {
    return getParameters(SIRI_AZURE_ET_UPDATER);
  }

  @Override
  public List<SiriAzureSXUpdaterParameters> getSiriAzureSXUpdaterParameters() {
    return getParameters(SIRI_AZURE_SX_UPDATER);
  }

  private <T> List<T> getParameters(Type key) {
    return (List<T>) configList.get(key);
  }

  public enum Type {
    // TODO: deprecated, remove in next major version
    BIKE_PARK(VehicleParkingUpdaterConfig::create),
    VEHICLE_PARKING(VehicleParkingUpdaterConfig::create),
    // TODO: deprecated, remove in next major version
    BIKE_RENTAL(VehicleRentalUpdaterConfig::create),
    VEHICLE_RENTAL(VehicleRentalUpdaterConfig::create),
    STOP_TIME_UPDATER(PollingStoptimeUpdaterConfig::create),
    WEBSOCKET_GTFS_RT_UPDATER(WebsocketGtfsRealtimeUpdaterConfig::create),
    MQTT_GTFS_RT_UPDATER(MqttGtfsRealtimeUpdaterConfig::create),
    REAL_TIME_ALERTS(GtfsRealtimeAlertsUpdaterConfig::create),
    VEHICLE_POSITIONS(VehiclePositionsUpdaterConfig::create),
    WINKKI_POLLING_UPDATER(WFSNotePollingGraphUpdaterConfig::create),
    SIRI_ET_UPDATER(SiriETUpdaterConfig::create),
    SIRI_ET_GOOGLE_PUBSUB_UPDATER(SiriETGooglePubsubUpdaterConfig::create),
    SIRI_VM_UPDATER(SiriVMUpdaterConfig::create),
    SIRI_SX_UPDATER(SiriSXUpdaterConfig::create),
    SIRI_AZURE_ET_UPDATER(SiriAzureETUpdaterConfig::create),
    SIRI_AZURE_SX_UPDATER(SiriAzureSXUpdaterConfig::create);

    Type(BiFunction<String, NodeAdapter, ?> factory) {
      this.factory = factory;
    }

    private final BiFunction<String, NodeAdapter, ?> factory;

    Object parseConfig(NodeAdapter nodeAdapter) {
      return factory.apply(this.name(), nodeAdapter);
    }
  }
}
