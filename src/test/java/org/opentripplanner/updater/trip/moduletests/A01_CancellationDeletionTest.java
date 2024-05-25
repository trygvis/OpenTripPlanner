package org.opentripplanner.updater.trip.moduletests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import com.google.transit.realtime.GtfsRealtime.TripDescriptor.ScheduleRelationship;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.opentripplanner.ext.siri.RealtimeTestEnvironment;
import org.opentripplanner.model.Timetable;
import org.opentripplanner.model.TimetableSnapshot;
import org.opentripplanner.transit.model.timetable.RealTimeState;
import org.opentripplanner.updater.trip.TripUpdateBuilder;

public class A01_CancellationDeletionTest {

  static List<Arguments> cases() {
    return List.of(
      Arguments.of(ScheduleRelationship.CANCELED, RealTimeState.CANCELED),
      Arguments.of(ScheduleRelationship.DELETED, RealTimeState.DELETED)
    );
  }

  @ParameterizedTest
  @MethodSource("cases")
  public void cancelledTrip(ScheduleRelationship relationship, RealTimeState state) {
    var env = new RealtimeTestEnvironment();
    var pattern1 = env.transitModel.getTransitModelIndex().getPatternForTrip().get(env.trip1);

    final int tripIndex1 = pattern1.getScheduledTimetable().getTripIndex(env.trip1.getId());

    var update = new TripUpdateBuilder(
      env.trip1.getId().getId(),
      env.serviceDate,
      relationship,
      env.timeZone
    )
      .buildList();
    var result = env.applyTripUpdates(update);

    assertEquals(1, result.successful());

    final TimetableSnapshot snapshot = env.gtfsSource.getTimetableSnapshot();
    final Timetable forToday = snapshot.resolve(pattern1, env.serviceDate);
    final Timetable schedule = snapshot.resolve(pattern1, null);
    assertNotSame(forToday, schedule);
    assertNotSame(forToday.getTripTimes(tripIndex1), schedule.getTripTimes(tripIndex1));

    var tripTimes = forToday.getTripTimes(tripIndex1);

    assertEquals(state, tripTimes.getRealTimeState());
  }
}
