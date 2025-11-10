package com.cm;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Comprehensive black-box JUnit5 tests for Period and Rate classes (Option C: full coverage).
 *
 * Filename/classname: MeudecChrisTestTask1
 */
public class TestMeudecChrisTask1 {

    //
    // -----------------------
    // Period tests
    // -----------------------
    //

    @Test
    public void period_validTypical() {
        Period p = new Period(7, 10);
        assertEquals(3, p.duration(), "duration should be end-start");
    }

    @Test
    public void period_validBoundaryStartZero() {
        Period p = new Period(0, 1);
        assertEquals(1, p.duration());
    }

    @Test
    public void period_validBoundaryEnd24() {
        Period p = new Period(23, 24);
        assertEquals(1, p.duration());
    }

    @Test
    public void period_invalidStartNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Period(-1, 5));
    }

    @Test
    public void period_invalidStartTooLarge() {
        assertThrows(IllegalArgumentException.class, () -> new Period(24, 25));
    }

    @Test
    public void period_invalidEndZero() {
        assertThrows(IllegalArgumentException.class, () -> new Period(0, 0));
    }

    @Test
    public void period_invalidEndTooLarge() {
        assertThrows(IllegalArgumentException.class, () -> new Period(4, 25));
    }

    @Test
    public void period_invalidNegativeDuration_equal() {
        assertThrows(IllegalArgumentException.class, () -> new Period(5, 5));
    }

    @Test
    public void period_invalidNegativeDuration_startGreater() {
        assertThrows(IllegalArgumentException.class, () -> new Period(6, 5));
    }

    @Test
    public void period_overlaps_true_partialOverlap() {
        Period a = new Period(8, 12);
        Period b = new Period(10, 14);
        assertTrue(a.overlaps(b));
        assertTrue(b.overlaps(a));
    }

    @Test
    public void period_overlaps_true_contained() {
        Period a = new Period(7, 15);
        Period b = new Period(8, 12);
        assertTrue(a.overlaps(b));
    }

    @Test
    public void period_overlaps_false_adjacent() {
        Period a = new Period(7, 10);
        Period b = new Period(10, 12);
        assertFalse(a.overlaps(b));
        assertFalse(b.overlaps(a));
    }

    @Test
    public void period_overlaps_false_disjoint() {
        Period a = new Period(1, 3);
        Period b = new Period(5, 7);
        assertFalse(a.overlaps(b));
    }

    @Test
    public void period_overlaps_nullArgThrows() {
        Period a = new Period(1, 2);
        assertThrows(IllegalArgumentException.class, () -> a.overlaps(null));
    }

    //
    // -----------------------
    // Rate constructor validation tests
    // -----------------------
    //

    @Test
    public void rate_constructor_validEmptyPeriods() {
        ArrayList<Period> reduced = new ArrayList<>();
        ArrayList<Period> normal = new ArrayList<>();
        new Rate(CarParkKind.STAFF, reduced, normal,
                new BigDecimal("5.00"), new BigDecimal("2.00"));
        // no exception means success
    }

    @Test
    public void rate_constructor_validNonOverlappingLists() {
        ArrayList<Period> reduced = new ArrayList<>();
        reduced.add(new Period(7, 10));
        ArrayList<Period> normal = new ArrayList<>();
        normal.add(new Period(10, 12));
        new Rate(CarParkKind.VISITOR, reduced, normal,
                new BigDecimal("4.50"), new BigDecimal("2.00"));
    }

    @Test
    public void rate_constructor_invalidNormalBelowZero() {
        assertThrows(IllegalArgumentException.class,
                () -> new Rate(CarParkKind.STUDENT, new ArrayList<>(), new ArrayList<>(), new BigDecimal("-0.01"), new BigDecimal("0.00")));
    }

    @Test
    public void rate_constructor_invalidReducedAboveTen() {
        assertThrows(IllegalArgumentException.class,
                () -> new Rate(CarParkKind.MANAGEMENT, new ArrayList<>(), new ArrayList<>(), new BigDecimal("1.00"), new BigDecimal("10.01")));
    }

    @Test
    public void rate_constructor_invalidNormalLessThanReduced() {
        assertThrows(IllegalArgumentException.class,
                () -> new Rate(CarParkKind.STAFF, new ArrayList<>(), new ArrayList<>(), new BigDecimal("2.00"), new BigDecimal("3.00")));
    }

    @Test
    public void rate_constructor_invalidOverlappingReducedPeriods() {
        ArrayList<Period> reduced = new ArrayList<>();
        reduced.add(new Period(7, 10));
        reduced.add(new Period(9, 12)); // overlaps
        ArrayList<Period> normal = new ArrayList<>();
        assertThrows(IllegalArgumentException.class,
                () -> new Rate(CarParkKind.VISITOR, reduced, normal, new BigDecimal("5"), new BigDecimal("2")));
    }

    @Test
    public void rate_constructor_invalidOverlappingNormalPeriods() {
        ArrayList<Period> reduced = new ArrayList<>();
        ArrayList<Period> normal = new ArrayList<>();
        normal.add(new Period(8, 12));
        normal.add(new Period(11, 16)); // overlaps
        assertThrows(IllegalArgumentException.class,
                () -> new Rate(CarParkKind.STUDENT, reduced, normal, new BigDecimal("5"), new BigDecimal("2")));
    }

    @Test
    public void rate_constructor_invalidOverlapAcrossLists() {
        ArrayList<Period> reduced = new ArrayList<>();
        reduced.add(new Period(7, 10));
        ArrayList<Period> normal = new ArrayList<>();
        normal.add(new Period(9, 12)); // overlaps with reduced
        assertThrows(IllegalArgumentException.class,
                () -> new Rate(CarParkKind.MANAGEMENT, reduced, normal, new BigDecimal("5"), new BigDecimal("2")));
    }

    @Test
    public void rate_constructor_nullKindThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Rate(null, new ArrayList<>(), new ArrayList<>(), new BigDecimal("1.00"), new BigDecimal("1.00")));
    }

    @Test
    public void rate_constructor_nullRatesThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> new Rate(CarParkKind.STAFF, new ArrayList<>(), new ArrayList<>(), null, new BigDecimal("1.00")));
        assertThrows(IllegalArgumentException.class,
                () -> new Rate(CarParkKind.STAFF, new ArrayList<>(), new ArrayList<>(), new BigDecimal("1.00"), null));
    }

    //
    // -----------------------
    // Rate.calculate tests
    // -----------------------
    //

    @Test
    public void calculate_freePeriod_returnsZero() {
        // no normal/reduced periods => all free
        Rate r = new Rate(CarParkKind.VISITOR, new ArrayList<>(), new ArrayList<>(), new BigDecimal("5.00"), new BigDecimal("2.00"));
        Period stay = new Period(2, 5); // 3 hours
        assertEquals(new BigDecimal("0.00"), r.calculate(stay));
    }

    @Test
    public void calculate_entirelyReducedPeriod() {
        ArrayList<Period> reduced = new ArrayList<>();
        reduced.add(new Period(7, 10));
        ArrayList<Period> normal = new ArrayList<>();
        Rate r = new Rate(CarParkKind.STAFF, reduced, normal, new BigDecimal("5.00"), new BigDecimal("2.00"));
        // stay fully in reduced 7..10 (3 hours)
        Period stay = new Period(7, 10);
        assertEquals(new BigDecimal("6.00"), r.calculate(stay)); // 3 * 2.00
    }

    @Test
    public void calculate_entirelyNormalPeriod() {
        ArrayList<Period> reduced = new ArrayList<>();
        ArrayList<Period> normal = new ArrayList<>();
        normal.add(new Period(8, 12));
        Rate r = new Rate(CarParkKind.STUDENT, reduced, normal, new BigDecimal("4.50"), new BigDecimal("2.00"));
        Period stay = new Period(8, 12); // 4 hours
        assertEquals(new BigDecimal("18.00"), r.calculate(stay)); // 4 * 4.50 = 18.00
    }

    @Test
    public void calculate_mixedFreeReducedNormal() {
        ArrayList<Period> reduced = new ArrayList<>();
        reduced.add(new Period(10, 12)); // reduced 10-12
        ArrayList<Period> normal = new ArrayList<>();
        normal.add(new Period(12, 15)); // normal 12-15
        Rate r = new Rate(CarParkKind.MANAGEMENT, reduced, normal, new BigDecimal("5.00"), new BigDecimal("2.00"));
        // stay 9 to 14 -> hours: 9,10,11,12,13 -> 5 hours
        // hour 9: free
        // 10,11: reduced -> 2 * 2 = 4
        // 12,13: normal -> 2 * 5 = 10
        // total = 14.00
        assertEquals(new BigDecimal("14.00"), r.calculate(new Period(9, 14)));
    }

    @Test
    public void calculate_exampleFromSpec() {
        // Spec example: enter 1:45 leave 5:23 -> period 1..6 (5 hours)
        // normal 2..5 normalRate=5 ; reduced after that rate=2
        ArrayList<Period> reduced = new ArrayList<>();
        reduced.add(new Period(5, 24)); // reduced applies after 5
        ArrayList<Period> normal = new ArrayList<>();
        normal.add(new Period(2, 5)); // normal 2..5
        Rate r = new Rate(CarParkKind.VISITOR, reduced, normal, new BigDecimal("5"), new BigDecimal("2"));
        // hours counted: 1,2,3,4,5 -> 5 hours
        // hour 1: free
        // 2,3,4: normal -> 3 * 5 = 15
        // 5: reduced -> 1 * 2 = 2
        // total = 17
        assertEquals(new BigDecimal("17.00"), r.calculate(new Period(1, 6)));
    }

    @Test
    public void calculate_singleHour_normal() {
        ArrayList<Period> normal = new ArrayList<>();
        normal.add(new Period(9, 10));
        Rate r = new Rate(CarParkKind.STAFF, new ArrayList<>(), normal, new BigDecimal("3.25"), new BigDecimal("1.00"));
        assertEquals(new BigDecimal("3.25"), r.calculate(new Period(9, 10)));
    }

    @Test
    public void calculate_singleHour_reduced() {
        ArrayList<Period> reduced = new ArrayList<>();
        reduced.add(new Period(14, 15));
        Rate r = new Rate(CarParkKind.STUDENT, reduced, new ArrayList<>(), new BigDecimal("5.00"), new BigDecimal("1.50"));
        assertEquals(new BigDecimal("1.50"), r.calculate(new Period(14, 15)));
    }

    @Test
    public void calculate_multipleDisjointReducedNormal() {
        ArrayList<Period> reduced = new ArrayList<>();
        reduced.add(new Period(7, 9));
        reduced.add(new Period(17, 19));
        ArrayList<Period> normal = new ArrayList<>();
        normal.add(new Period(9, 17));
        Rate r = new Rate(CarParkKind.MANAGEMENT, reduced, normal, new BigDecimal("6.00"), new BigDecimal("3.00"));
        // stay 6..18 -> hours 6..17 (12 hours)
        // 6: free
        // 7,8: reduced -> 2 * 3 = 6
        // 9..16: normal -> 8 * 6 = 48
        // 17: reduced -> 1 * 3 = 3
        // total = 57.00
        assertEquals(new BigDecimal("57.00"), r.calculate(new Period(6, 18)));
    }

    @Test
    public void calculate_rounding_halfUp() {
        // rates with fractional values that produce repeating decimals when summed if mis-handled.
        ArrayList<Period> normal = new ArrayList<>();
        normal.add(new Period(10, 13)); // 3 hours
        Rate r = new Rate(CarParkKind.STAFF, new ArrayList<>(), normal, new BigDecimal("1.335"), new BigDecimal("0.00"));
        // 3 * 1.335 = 4.005 -> round HALF_UP -> 4.01
        assertEquals(new BigDecimal("4.01"), r.calculate(new Period(10, 13)));
    }

    @Test
    public void calculate_nullStayThrows() {
        Rate r = new Rate(CarParkKind.VISITOR, new ArrayList<>(), new ArrayList<>(), new BigDecimal("1.00"), new BigDecimal("1.00"));
        assertThrows(IllegalArgumentException.class, () -> r.calculate(null));
    }

    @Test
    public void calculate_zeroRatesAreAllowed() {
        ArrayList<Period> normal = new ArrayList<>();
        normal.add(new Period(8, 10));
        Rate r = new Rate(CarParkKind.STUDENT, new ArrayList<>(), normal, new BigDecimal("0.00"), new BigDecimal("0.00"));
        assertEquals(new BigDecimal("0.00"), r.calculate(new Period(8, 10)));
    }

    @Test
    public void calculate_boundaryStartZeroAndEnd24() {
        // normal 0..24 with rate 1.00 => full day cost 24.00
        ArrayList<Period> normal = new ArrayList<>();
        normal.add(new Period(0, 24));
        Rate r = new Rate(CarParkKind.MANAGEMENT, new ArrayList<>(), normal, new BigDecimal("1.00"), new BigDecimal("0.50"));
        assertEquals(new BigDecimal("24.00"), r.calculate(new Period(0, 24)));
    }

    @Test
    public void calculate_whenReducedEqualsNormalWorks() {
        // normalRate == reducedRate allowed
        ArrayList<Period> reduced = new ArrayList<>();
        reduced.add(new Period(7, 9));
        ArrayList<Period> normal = new ArrayList<>();
        normal.add(new Period(9, 11));
        Rate r = new Rate(CarParkKind.STAFF, reduced, normal, new BigDecimal("2.00"), new BigDecimal("2.00"));
        assertEquals(new BigDecimal("4.00"), r.calculate(new Period(7, 9)));
        assertEquals(new BigDecimal("4.00"), r.calculate(new Period(9, 11)));
    }

    @Test
    public void calculate_overlapShouldNotOccurAssumption() {
        // If there were overlaps between reduced and normal lists constructor should throw;
        ArrayList<Period> reduced = new ArrayList<>();
        reduced.add(new Period(8, 12));
        ArrayList<Period> normal = new ArrayList<>();
        normal.add(new Period(12, 16));
        Rate r = new Rate(CarParkKind.VISITOR, reduced, normal, new BigDecimal("3.00"), new BigDecimal("1.00"));
        // sanity check: calculates fine
        assertEquals(new BigDecimal("8.00"), r.calculate(new Period(8, 12))); // 4 * 1.00
    }

    //
    // Misc negative tests that exercise edge conditions and invalid constructor combos
    //

    @Test
    public void rate_constructor_ratesUpperBoundaryOk() {
        new Rate(CarParkKind.MANAGEMENT, new ArrayList<>(), new ArrayList<>(), new BigDecimal("10.00"), new BigDecimal("0.00"));
    }

    @Test
    public void rate_constructor_ratesLowerBoundaryOk() {
        new Rate(CarParkKind.STAFF, new ArrayList<>(), new ArrayList<>(), new BigDecimal("0.00"), new BigDecimal("0.00"));
    }

    @Test
    public void rate_constructor_reducedPeriodsNullTreatedAsEmpty() {
        // constructor allows null lists (we treat as empty) -> should not throw
        new Rate(CarParkKind.STUDENT, null, null, new BigDecimal("1.00"), new BigDecimal("0.50"));
    }

    @Test
    public void rate_calculate_partialHoursAreCountedAsFull_asModelledByPeriodIntegers() {
        // Because external code converts fractional times to integer period bounds before calling Rate,
        // Rate works on integer hours: a stay (1,6) yields 5 hours
        ArrayList<Period> normal = new ArrayList<>();
        normal.add(new Period(2, 5));
        ArrayList<Period> reduced = new ArrayList<>();
        reduced.add(new Period(5, 6));
        Rate r = new Rate(CarParkKind.VISITOR, reduced, normal, new BigDecimal("5.00"), new BigDecimal("2.00"));
        assertEquals(new BigDecimal("17.00"), r.calculate(new Period(1, 6)));
    }

    @Test
    public void calculate_adjacentPeriods_countedCorrectly() {
        ArrayList<Period> reduced = new ArrayList<>();
        reduced.add(new Period(7, 10));
        ArrayList<Period> normal = new ArrayList<>();
        normal.add(new Period(10, 13));
        Rate r = new Rate(CarParkKind.STAFF, reduced, normal, new BigDecimal("4.00"), new BigDecimal("1.00"));
        // stay 6..13 -> 6: free, 7-9 reduced(3h), 10-12 normal(3h)
        assertEquals(new BigDecimal("15.00"), r.calculate(new Period(6, 13))); // 3*1 + 3*4 = 3 + 12 = 15
    }

    @Test
    public void calculate_fullCoverageMultiplePeriods() {
        ArrayList<Period> reduced = new ArrayList<>();
        reduced.add(new Period(0, 2));
        reduced.add(new Period(22, 24));
        ArrayList<Period> normal = new ArrayList<>();
        normal.add(new Period(9, 17));
        Rate r = new Rate(CarParkKind.MANAGEMENT, reduced, normal, new BigDecimal("8.25"), new BigDecimal("2.75"));
        // stay 0..24 -> total = sum of all hours (2 reduced + 8 normal + 14 free)
        BigDecimal expected = new BigDecimal("2").multiply(new BigDecimal("2.75"))
                .add(new BigDecimal("8").multiply(new BigDecimal("8.25")))
                .setScale(2, java.math.RoundingMode.HALF_UP);
        assertEquals(expected, r.calculate(new Period(0, 24)));
    }

    @Test
    public void calculate_whenPeriodTouches24Edge() {
        ArrayList<Period> normal = new ArrayList<>();
        normal.add(new Period(20, 24));
        Rate r = new Rate(CarParkKind.VISITOR, new ArrayList<>(), normal, new BigDecimal("2.50"), new BigDecimal("1.00"));
        assertEquals(new BigDecimal("10.00"), r.calculate(new Period(20, 24))); // 4 * 2.50
    }

    @Test
    public void calculate_whenPeriodTouchesZeroEdge() {
        ArrayList<Period> normal = new ArrayList<>();
        normal.add(new Period(0, 3));
        Rate r = new Rate(CarParkKind.VISITOR, new ArrayList<>(), normal, new BigDecimal("2.50"), new BigDecimal("1.00"));
        assertEquals(new BigDecimal("7.50"), r.calculate(new Period(0, 3))); // 3 * 2.50
    }

    //
    // Final sanity test: a variety of small random-ish checks to ensure behaviour is consistent
    //
    @Test
    public void calculate_variousSanityChecks() {
        ArrayList<Period> reduced = new ArrayList<>();
        reduced.add(new Period(1, 4));
        ArrayList<Period> normal = new ArrayList<>();
        normal.add(new Period(4, 6));
        Rate r = new Rate(CarParkKind.STAFF, reduced, normal, new BigDecimal("3.33"), new BigDecimal("1.11"));
        // stay 0..6 -> hours: 0 free, 1-3 reduced (3h), 4-5 normal (2h)
        BigDecimal expected = new BigDecimal("3").multiply(new BigDecimal("1.11"))
                .add(new BigDecimal("2").multiply(new BigDecimal("3.33"))).setScale(2, java.math.RoundingMode.HALF_UP);
        assertEquals(expected, r.calculate(new Period(0, 6)));
    }
}

