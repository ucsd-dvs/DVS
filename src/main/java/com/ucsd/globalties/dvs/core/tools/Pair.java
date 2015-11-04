package com.ucsd.globalties.dvs.core.tools;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * A simple data structure that represents a set of 2 objects.
 * @author Rahul
 *
 * @param <L>
 * @param <R>
 */
@AllArgsConstructor
public class Pair<L, R> {
  @Getter @Setter
  private L left;
  
  @Getter @Setter
  private R right;
}
