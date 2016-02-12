package sightfinder.gate;

import gate.*;
import gate.util.GateException;
import gate.util.Out;
import gate.util.persistence.PersistenceManager;
import sightfinder.model.Landmark;
import sightfinder.service.LandmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Created by krasimira on 11.02.16.
 */
@Service
public class LocationsPipeline {

    private static String LOOKUP_ANNOTATION = "Lookup";
    private CorpusController corpusController;

    @Autowired
    private LandmarkService landmarkService;

    private Iterable<Landmark> landmarks;

    public void initAnnie() throws GateException, IOException {
        File pipelineFile = getPipelineGappFile();
        corpusController =
                (CorpusController) PersistenceManager.loadObjectFromFile(pipelineFile);
    }


    public void setCorpus(Corpus corpus) {
        corpusController.setCorpus(corpus);
    }

    public void execute() throws GateException {
        Out.prln("Running ANNIE...");
        corpusController.execute();
        Out.prln("...ANNIE complete");
    }

    public Corpus getLandmarksCorpus() throws GateException {
        Corpus corpus = Factory.newCorpus("Landmarks corpus");


        int count = 0;
        for (Landmark landmark : landmarks) {
            if (count++ < 10) {
                Document landmarkDocument = Factory.newDocument(landmark.getDescription());
                corpus.add(landmarkDocument);
            } else {
                break;
            }
        }

        return corpus;
    }

    public LocationsPipeline getPipelineWithCorpus() throws GateException, IOException {
        LocationsPipeline pipeline = new LocationsPipeline();
        pipeline.initAnnie();
        pipeline.setCorpus(getLandmarksCorpus());

        return pipeline;
    }

    public void listAnnotations() throws GateException, IOException {

        Gate.init();

        landmarks = landmarkService.getLandmarks();

        LocationsPipeline pipeline = getPipelineWithCorpus();
        pipeline.execute();

        Iterator annotatedLandmarksIterator = pipeline.corpusController.getCorpus().iterator();
        Map<String, List<Landmark>> locationToLandmarks = new HashMap<>();

        while (annotatedLandmarksIterator.hasNext()) {
            Document landmarkDocument = (Document) annotatedLandmarksIterator.next();
            for (Annotation annotation : landmarkDocument.getAnnotations()) {
                if (annotation.getType().equals(LOOKUP_ANNOTATION)) {
                    long startOffset = annotation.getStartNode().getOffset();
                    long endOffset = annotation.getEndNode().getOffset();
                    String locationToken = landmarkDocument.getContent().getContent(startOffset, endOffset).toString();

                    if (!locationToLandmarks.containsKey(locationToken)) {
                        locationToLandmarks.put(locationToken, new ArrayList<>());
                    }

                    //locationToLandmarks.get(locationToken).add(landmarkDocument)
                    System.out.println(locationToken);
                }
            }
        }

        System.out.print("The end!!");
    }

    private static File getPipelineGappFile() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        URL pipelineResource = classloader.getResource("gate/ling-pipe-pipeline-reduced.gapp");
        File pipelineFile = null;
        try {
            pipelineFile = new File(pipelineResource.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();;
        }

        return pipelineFile;
    }


//    // for each document, get an XML document with the
//    // person and location names added
//    Iterator iter = corpus.iterator();
//    int count = 0;
//    String startTagPart_1 = "<span GateID=\"";
//    String startTagPart_2 = "\" title=\"";
//    String startTagPart_3 = "\" style=\"background:Red;\">";
//    String endTag = "</span>";
//
//    while(iter.hasNext()) {
//        Document doc = (Document) iter.next();
//        AnnotationSet defaultAnnotSet = doc.getAnnotations();
//        Set annotTypesRequired = new HashSet();
//        annotTypesRequired.add("Person");
//        annotTypesRequired.add("Location");
//        Set<Annotation> peopleAndPlaces =
//                new HashSet<Annotation>(defaultAnnotSet.get(annotTypesRequired));
//
//        FeatureMap features = doc.getFeatures();
//        String originalContent = (String)
//                features.get(GateConstants.ORIGINAL_DOCUMENT_CONTENT_FEATURE_NAME);
//        RepositioningInfo info = (RepositioningInfo)
//                features.get(GateConstants.DOCUMENT_REPOSITIONING_INFO_FEATURE_NAME);
//
//        ++count;
//        File file = new File("StANNIE_" + count + ".HTML");
//        Out.prln("File name: '"+file.getAbsolutePath()+"'");
//        if(originalContent != null && info != null) {
//            Out.prln("OrigContent and reposInfo existing. Generate file...");
//
//            Iterator it = peopleAndPlaces.iterator();
//            Annotation currAnnot;
//            SortedAnnotationList sortedAnnotations = new SortedAnnotationList();
//
//            while(it.hasNext()) {
//                currAnnot = (Annotation) it.next();
//                sortedAnnotations.addSortedExclusive(currAnnot);
//            } // while
//
//            StringBuffer editableContent = new StringBuffer(originalContent);
//            long insertPositionEnd;
//            long insertPositionStart;
//            // insert anotation tags backward
//            Out.prln("Unsorted annotations count: "+peopleAndPlaces.size());
//            Out.prln("Sorted annotations count: "+sortedAnnotations.size());
//            for(int i=sortedAnnotations.size()-1; i>=0; --i) {
//                currAnnot = (Annotation) sortedAnnotations.get(i);
//                insertPositionStart =
//                        currAnnot.getStartNode().getOffset().longValue();
//                insertPositionStart = info.getOriginalPos(insertPositionStart);
//                insertPositionEnd = currAnnot.getEndNode().getOffset().longValue();
//                insertPositionEnd = info.getOriginalPos(insertPositionEnd, true);
//                if(insertPositionEnd != -1 && insertPositionStart != -1) {
//                    editableContent.insert((int)insertPositionEnd, endTag);
//                    editableContent.insert((int)insertPositionStart, startTagPart_3);
//                    editableContent.insert((int)insertPositionStart,
//                            currAnnot.getType());
//                    editableContent.insert((int)insertPositionStart, startTagPart_2);
//                    editableContent.insert((int)insertPositionStart,
//                            currAnnot.getId().toString());
//                    editableContent.insert((int)insertPositionStart, startTagPart_1);
//                } // if
//            } // for
//
//            FileWriter writer = new FileWriter(file);
//            writer.write(editableContent.toString());
//            writer.close();
//        } // if - should generate
//        else if (originalContent != null) {
//            Out.prln("OrigContent existing. Generate file...");
//
//            Iterator it = peopleAndPlaces.iterator();
//            Annotation currAnnot;
//            SortedAnnotationList sortedAnnotations = new SortedAnnotationList();
//
//            while(it.hasNext()) {
//                currAnnot = (Annotation) it.next();
//                sortedAnnotations.addSortedExclusive(currAnnot);
//            } // while
//
//            StringBuffer editableContent = new StringBuffer(originalContent);
//            long insertPositionEnd;
//            long insertPositionStart;
//            // insert anotation tags backward
//            Out.prln("Unsorted annotations count: "+peopleAndPlaces.size());
//            Out.prln("Sorted annotations count: "+sortedAnnotations.size());
//            for(int i=sortedAnnotations.size()-1; i>=0; --i) {
//                currAnnot = (Annotation) sortedAnnotations.get(i);
//                insertPositionStart =
//                        currAnnot.getStartNode().getOffset().longValue();
//                insertPositionEnd = currAnnot.getEndNode().getOffset().longValue();
//                if(insertPositionEnd != -1 && insertPositionStart != -1) {
//                    editableContent.insert((int)insertPositionEnd, endTag);
//                    editableContent.insert((int)insertPositionStart, startTagPart_3);
//                    editableContent.insert((int)insertPositionStart,
//                            currAnnot.getType());
//                    editableContent.insert((int)insertPositionStart, startTagPart_2);
//                    editableContent.insert((int)insertPositionStart,
//                            currAnnot.getId().toString());
//                    editableContent.insert((int)insertPositionStart, startTagPart_1);
//                } // if
//            } // for
//
//            FileWriter writer = new FileWriter(file);
//            writer.write(editableContent.toString());
//            writer.close();
//        }
//        else {
//            Out.prln("Content : "+originalContent);
//            Out.prln("Repositioning: "+info);
//        }
//
//        String xmlDocument = doc.toXml(peopleAndPlaces, false);
//        String fileName = new String("StANNIE_toXML_" + count + ".HTML");
//        FileWriter writer = new FileWriter(fileName);
//        writer.write(xmlDocument);
//        writer.close();
//
//    } // for each doc
//} // main
//
///**
// *
// */
//public static class SortedAnnotationList extends Vector {
//    public SortedAnnotationList() {
//        super();
//    } // SortedAnnotationList
//
//    public boolean addSortedExclusive(Annotation annot) {
//        Annotation currAnot = null;
//
//        // overlapping check
//        for (int i=0; i<size(); ++i) {
//            currAnot = (Annotation) get(i);
//            if(annot.overlaps(currAnot)) {
//                return false;
//            } // if
//        } // for
//
//        long annotStart = annot.getStartNode().getOffset().longValue();
//        long currStart;
//        // insert
//        for (int i=0; i < size(); ++i) {
//            currAnot = (Annotation) get(i);
//            currStart = currAnot.getStartNode().getOffset().longValue();
//            if(annotStart < currStart) {
//                insertElementAt(annot, i);
//          /*
//           Out.prln("Insert start: "+annotStart+" at position: "+i+" size="+size());
//           Out.prln("Current start: "+currStart);
//           */
//                return true;
//            } // if
//        } // for
//
//        int size = size();
//        insertElementAt(annot, size);
////Out.prln("Insert start: "+annotStart+" at size position: "+size);
//        return true;
//    } // addSorted
//}
}
