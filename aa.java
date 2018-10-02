import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

public class BFSInRDFWithJena {

    public static List<List<Resource>> BFS( final Model model, final Queue<List<Resource>> queue, final int depth ) {
        final List<List<Resource>> results = new ArrayList<>();
        while ( !queue.isEmpty() ) {
            final List<Resource> path = queue.poll();
            results.add( path );
            if ( path.size() < depth ) {
                final Resource last = path.get( path.size() - 1 );
                final StmtIterator stmt = model.listStatements( null, RDFS.subClassOf, last );
                while ( stmt.hasNext() ) {
                    final List<Resource> extPath = new ArrayList<>( path );
                    extPath.add( stmt.next().getSubject().asResource() );
                    queue.offer( extPath );
                }
            }
        }
        return results;
    }

    public static void main( final String[] args ) throws IOException {
        final Model model = ModelFactory.createDefaultModel();
        try ( final InputStream in = BFSInRDFWithJena.class.getClassLoader().getResourceAsStream( "camera.owl" ) ) {
            model.read( in, null );
        }

        // setup the initial queue
        final Queue<List<Resource>> queue = new LinkedList<>();
        final List<Resource> thingPath = new ArrayList<>();
        thingPath.add( OWL.Thing );
        queue.offer( thingPath );

        // Get the paths, and display them
        final List<List<Resource>> paths = BFS( model, queue, 4 );
        for ( List<Resource> path : paths ) {
            System.out.println( path );
        }
    }
}
