/*
  The MIT License (MIT)

  Copyright (c) 2014-2017 Marc de Verdelhan & respective authors (see AUTHORS)

  Permission is hereby granted, free of charge, to any person obtaining a copy of
  this software and associated documentation files (the "Software"), to deal in
  the Software without restriction, including without limitation the rights to
  use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
  the Software, and to permit persons to whom the Software is furnished to do so,
  subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ta4j.core;

/**
 * A trading strategy.
 * <p></p>
 * A strategy is a pair of complementary {@link Rule rules}. It may recommend to enter or to exit.
 * Recommendations are based respectively on the entry rule or on the exit rule.
 */
public interface Strategy {

    /**
     * @return the name of the strategy
     */
    String getName();
	
    /**
     * @return the description of the strategy
     */
    String getHint();
	
    /**
     * @return the entry rule
     */
    Rule getEntryRule();
    
    /**
     * @return the exit rule
     */
    Rule getExitRule();
	
    /**
     * @param strategy the other strategy
     * @return the AND combination of two {@link Strategy strategies}
     */
    Strategy and(Strategy strategy);
    
    /**
     * @param strategy the other strategy
     * @return the OR combination of two {@link Strategy strategies}
     */
    Strategy or(Strategy strategy);

    /**
     * @param name the name of the strategy
     * @param strategy the other strategy
     * @param unstablePeriod number of ticks that will be strip off for this strategy
     * @return the AND combination of two {@link Strategy strategies}
     */
    Strategy and(String name, Strategy strategy, int unstablePeriod);
    
    /**
     * @param name the name of the strategy
     * @param strategy the other strategy
     * @param unstablePeriod number of ticks that will be strip off for this strategy
     * @return the OR combination of two {@link Strategy strategies}
     */
    Strategy or(String name, Strategy strategy, int unstablePeriod);
    
    /**
     * @return the opposite of the {@link Strategy strategy}
     */
    Strategy opposite();
	
    /**
     * @param hint the description of the strategy
     */
    void setHint(String hint);
    
    /**
     * @param unstablePeriod number of ticks that will be strip off for this strategy
     */
    void setUnstablePeriod(int unstablePeriod);
    
    /**
     * @return unstablePeriod number of ticks that will be strip off for this strategy
     */
    int getUnstablePeriod();
    
    /**
     * @param index a tick index
     * @return true if this strategy is unstable at the provided index, false otherwise (stable)
     */
    boolean isUnstableAt(int index);
    
    /**
     * @param index the tick index
     * @param tradingRecord the potentially needed trading history
     * @return true to recommend an order, false otherwise (no recommendation)
     */
    default boolean shouldOperate(int index, TradingRecord tradingRecord) {
        Trade trade = tradingRecord.getCurrentTrade();
        if (trade.isNew()) {
            return shouldEnter(index, tradingRecord);
        } else if (trade.isOpened()) {
            return shouldExit(index, tradingRecord);
        }
        return false;
    }

    /**
     * @param index the tick index
     * @return true to recommend to enter, false otherwise
     */
    default boolean shouldEnter(int index) {
        return shouldEnter(index, null);
    }

    /**
     * @param index the tick index
     * @param tradingRecord the potentially needed trading history
     * @return true to recommend to enter, false otherwise
     */
    default boolean shouldEnter(int index, TradingRecord tradingRecord) {
        if (isUnstableAt(index)) {
            return false;
        }
        return getEntryRule().isSatisfied(index, tradingRecord);
    }

    /**
     * @param index the tick index
     * @return true to recommend to exit, false otherwise
     */
    default boolean shouldExit(int index) {
        return shouldExit(index, null);
    }

    /**
     * @param index the tick index
     * @param tradingRecord the potentially needed trading history
     * @return true to recommend to exit, false otherwise
     */
    default boolean shouldExit(int index, TradingRecord tradingRecord) {
        if (isUnstableAt(index)) {
            return false;
        }
        return getExitRule().isSatisfied(index, tradingRecord);
    }
}
