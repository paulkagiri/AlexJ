package sairepa.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

public class ActListFactoryLayout implements Iterable<ActListFactory>
{
  private String[] setNames;
  private ActListFactory[][] factories;
  private Model m;

  public ActListFactoryLayout(Model m, String[] setNames, ActListFactory[][] factories) {
    this.setNames = setNames;
    this.factories = factories;
    this.m = m;
  }

  public Model getModel() {
    return m;
  }

  public int getNumberOfFactories() {
    int i = 0;
    for (ActListFactory[] fs : factories) i += fs.length;
    return i;
  }

  public class ActListFactoryIterator implements Iterator<ActListFactory> {
    private int posNextX = 0;
    private int posNextY = 0;

    public ActListFactoryIterator() {
      posNextX = 0;
      posNextY = 0;
    }

    public boolean hasNext() {
      return (posNextX < factories.length);
    }

    public ActListFactory next() {
      ActListFactory factory = factories[posNextX][posNextY];

      posNextY++;
      if (posNextY >= factories[posNextX].length) {
	posNextX++;
	posNextY = 0;
      }

      return factory;
    }

    public void remove() {
      throw new UnsupportedOperationException(">o_/  ==>  >x_/");
    }
  }

  public String[] getFactorySetNames() {
    return setNames;
  }

  public ActListFactory[][] getFactories() {
    return factories;
  }

  public Iterator<ActListFactory> iterator() {
    return new ActListFactoryIterator();
  }
}
