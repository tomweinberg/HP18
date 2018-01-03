package mutation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.vecmath.Vector3f;

import main.Configuration;
import main.Conformation;
import main.Dimensions;
import main.MonomerDirection;
import main.Pair;
import main.Protein;
import math.MatrixManipulation;

public class MutationPreDefined implements MutationAlgorithm {
	private Dimensions dimensions;
	private Conformation originalConformationIn = null;
	private Conformation originalConformationOut = null;

	/** The set of mutation libraries */
	public MutationLibrary[] mutationLibraries;
	private MutationLibrary mutationLibrary;

	/** The mutation list. unsorted. just loaded from the file */
	private List<Vector<Mutation>> mutationList;

	private MatrixManipulation matrixMan;

	private float pm;
	private Random random;

	/**
	 * The following collections are used by mutate(), and are saved as members
	 * to improve performance.
	 */
	private ArrayList<MutationLibraryEntry> candidates = new ArrayList<MutationLibraryEntry>();
	private ArrayList<Vector3f> proteinPositions = new ArrayList<Vector3f>();

	/** times setConformation() failed. used for performance measuring */
	public int numOfFailers = 0;

	/** times setConformation() called. used for performance measuring */
	public int numOfIterations = 0;

	/**
	 * temp Vectors used by activateMutationOnProtein(). Declared as members to
	 * avoid "new".
	 */
	private Vector3f v1 = new Vector3f();
	private Vector3f v2 = new Vector3f();
	private Vector3f v3 = new Vector3f();

	
	public MutationPreDefined(Configuration config) {
		matrixMan = new MatrixManipulation(config.dimensions);
		this.random = config.random;
		dimensions = config.dimensions;
		loadData(config.mutationsFileName);
		buildDictionaries();
	}

	/**
	 * Loading Mutations data from file
	 * 
	 * @param fileName
	 *            the file name
	 * 
	 */

	private void loadData(String fileName) {
		mutationList = readFromFile(fileName);
	}
	
	/**
	 * read the mutation list data from the a file
	 * 
	 * @param filename
	 *            the name and path of the file the linklist data are in
	 * @return a linklist reprusent the mutations
	 */
	public LinkedList<Vector<Mutation>> readFromFile(String filename) {
		try {
			LinkedList<Vector<Mutation>> list = new LinkedList<Vector<Mutation>>();
			Vector<Mutation> currentVector = null;

			// filename="a.txt";
			FileReader input = new FileReader(filename);
			BufferedReader bufRead = new BufferedReader(input);

			String line = bufRead.readLine();
			StringTokenizer sT;
			Mutation mutation;
			String chainStr, adjenciesStr, pairStr, firstVectorStr, lastVectorStr;
			Pair<Integer, Integer> pair;
			Integer pairNum1, pairNum2;
			while (line != null) {
				if (line.charAt(0) != '#' && line.charAt(0) != '!'
					&& line.trim().length() != 0) {// Skip Comments\ headers
					// \ empty lines
					if (currentVector == null) {
						bufRead.close();
						throw new RuntimeException(
								"file: "
								+ filename
								+ " , is not a proper file becouse there is vector before header");
					}
					sT = new StringTokenizer(line, "\t");

					chainStr = sT.nextToken();
					chainStr = chainStr.substring(1, chainStr.length() - 1);
					firstVectorStr = sT.nextToken();
					firstVectorStr = firstVectorStr.substring(1,
							firstVectorStr.length() - 1);
					lastVectorStr = sT.nextToken();
					lastVectorStr = lastVectorStr.substring(1,
							lastVectorStr.length() - 1);

					adjenciesStr = sT.nextToken();
					adjenciesStr = adjenciesStr.substring(1,
							adjenciesStr.length() - 1);

					mutation = new Mutation(stringToVector(firstVectorStr),
							stringToVector(lastVectorStr), chainStr, 0);

					if (adjenciesStr.length() != 0) {
						sT = new StringTokenizer(adjenciesStr, ":");
						while (sT.hasMoreTokens()) {
							pairStr = sT.nextToken();
							pairNum1 = new Integer(pairStr.substring(
									pairStr.indexOf('<') + 1,
									pairStr.indexOf(',')));
							pairNum2 = new Integer(pairStr.substring(
									pairStr.indexOf(',') + 1).replace(">", ""));
							pair = new Pair<Integer, Integer>(pairNum1,
									pairNum2);

							mutation.addToAdjacencyList(pair);
						}
					}
					currentVector.add(mutation);
				} else if (line.charAt(0) == '!') // if header
				{
					currentVector = new Vector<Mutation>();
					list.addLast(currentVector);
				}
				line = bufRead.readLine();
			}
			bufRead.close();
			return list;
		} catch (Exception e) {

			e.printStackTrace();
		}

		return null;
	}

	/**
	 * String to vector.
	 * 
	 * @param vectorStr
	 *            the vector string representation
	 * 
	 * @return the vector3f
	 */
	private Vector3f stringToVector(String vectorStr) {
		vectorStr = vectorStr.substring(1, vectorStr.length() - 1);
		StringTokenizer sT = new StringTokenizer(vectorStr, ",");
		return new Vector3f(Float.parseFloat(sT.nextToken()),
				Float.parseFloat(sT.nextToken()), Float.parseFloat(sT
						.nextToken()));
	}

	/**
	 * Building the mutationLibrary based on the data loaded on to mutationList
	 * field mutation length is equal to the number of monomers that participate
	 * in the mutation
	 */
	private void buildDictionaries() {
		mutationLibraries = new MutationLibrary[mutationList.size() + 2];
		int currentDictionaryIndex = 2; // minimum mutation length is 2
		for (Vector<Mutation> vector : mutationList) {
			mutationLibraries[currentDictionaryIndex] = new MutationLibrary();
			for (Mutation mutation : vector) {
				mutation.setProbability(pm);
				addLibraryEntry(mutation,
						mutationLibraries[currentDictionaryIndex]);
			}
			currentDictionaryIndex++;
		}
	}

	/**
	 * Adds a mutation to a {@link MutationLibrary}. Six versions of of each
	 * mutation are added to mutationLibrary entry. the method fill's for the
	 * given mutation all the possible vector in the given rotations
	 * <p>
	 * 0 degrees on axis x
	 * <p>
	 * 90 degrees on axis x
	 * <p>
	 * -90 degrees on axis x
	 * <p>
	 * 180 degrees on axis x
	 * <p>
	 * 90 degrees on axis y
	 * <p>
	 * -90 degrees on axis y
	 * 
	 * @param mutation
	 *            the mutation
	 * @param library
	 *            the mutationLibrary
	 */
	private void addLibraryEntry(Mutation mutation, MutationLibrary library) {
		Vector3f positionVector = new Vector3f();

		// TODO: check for duplicity in the Dictionary while putting mutation
		positionVector.sub(mutation.getLastMonomerVector(),
				mutation.getFirstMonomerVector());
		// put in library as it is
		MutationLibraryEntry newEntry = new MutationLibraryEntry(mutation, 'x',
				0, MonomerDirection.FORWARD, dimensions, matrixMan);
		library.put(positionVector, newEntry);

		if (dimensions == Dimensions.THREE) {
			// rotation of 90 deg around x axis
			addToLib(mutation, library, positionVector, (Math.PI / 2), 'x',
					MonomerDirection.UP);
			// rotation of -90 deg around x axis
			addToLib(mutation, library, positionVector, (-Math.PI / 2), 'x',
					MonomerDirection.DOWN);
		}

		// rotation of 180 deg around x axis
		// TODO: check definition in here !!!
		addToLib(mutation, library, positionVector, Math.PI, 'x',
				MonomerDirection.FORWARD);
		// rotation of 90 deg around y axis

		addToLib(mutation, library, positionVector, (Math.PI / 2), 'z',
				MonomerDirection.LEFT);
		// rotation of -90 deg around y axis
		addToLib(mutation, library, positionVector, (-Math.PI / 2), 'z',
				MonomerDirection.RIGHT);
	}

	private void addToLib(Mutation mutation, MutationLibrary library,
			Vector3f positionVector, double degree, char axis,
			MonomerDirection relative) {

		if ((dimensions == Dimensions.THREE) || (relative.ordinal() < 3)) {
			MutationLibraryEntry newEntry = new MutationLibraryEntry(mutation,
					axis, degree, relative, dimensions, matrixMan);
			library.put(matrixMan.LorentzTransformation(positionVector, axis,
					degree), newEntry);
		}
	}

	/**
	 * 
	 * @param list
	 *            the list of MutationLibraryEntry
	 * @return random number between 0 and the list length
	 */
	private int selectMutationNumber(Collection<MutationLibraryEntry> list) {
		return random.nextInt(list.size());
	}

	/**
	 * change protein by the mutation
	 * 
	 * @param protein
	 *            the protein the changes should be saved to
	 * @param originalProtein
	 *            the protein before the change
	 * @param entry
	 *            the MutationLibraryEntry
	 * @param mutationStartMonomer
	 *            for where we should start the change
	 * @param mutationEndMonomer
	 *            where the change should end
	 * @param originalConformation
	 *            the original protein confirmation
	 * @return true upon success
	 */
	private boolean activateMutationOnProtein(Protein protein,
			Protein originalProtein, MutationLibraryEntry entry,
			int mutationStartMonomer, int mutationEndMonomer,
			Conformation originalConformation) {
		if (originalConformation.size() == 0)
			throw new RuntimeException("originalConformation.size() == 0");
		numOfIterations++;
		int size = protein.size();
		// originalConformation=originalProtein.conformation;

		MonomerDirection[] mutationArray = entry.getMutation().getConfomation();
		Conformation newConformation = new Conformation(size);
		int monomerNumber;

		int temp, i;
		if (mutationEndMonomer >= size)
			throw new RuntimeException("mutation too long");

		if (originalConformation.size() <= mutationStartMonomer)
			throw new RuntimeException(
					"original conformation may be corupted original conformation size ="
					+ originalConformation.size()
					+ "\n mutationStartMonomer=" + mutationStartMonomer
					+ "\n mutationEndMonomer=" + mutationEndMonomer
					+ "\n protain.size()=" + size+"\n"
					+ originalConformation);
		// conformation before mutation will be added normally.
		for (monomerNumber = 0; monomerNumber <= mutationStartMonomer; monomerNumber++)
			newConformation.add(originalConformation.get(monomerNumber));

		// First monomer after start of mutation.
		// The conformation(direction) of this momomer is not necessarily as
		// written in mutation conformation,
		// therefore we will calculate its direction according to position of 3
		// vectors.
		Vector3f mutationStartPos = originalProtein.get(mutationStartMonomer)
		.getR();
		v1.set(originalProtein.get(mutationStartMonomer - 1).getR());
		v2.set(mutationStartPos);
		v3.set(mutationStartPos);
		v3.add(entry.positionOffsets.get(1));
		newConformation.add(matrixMan.getDirection(v1, v2, v3));

		// Conformation of mutation
		i = 1;
		for (monomerNumber = mutationStartMonomer + 2; monomerNumber <= mutationEndMonomer; monomerNumber++, i++) {
			newConformation.add(mutationArray[i]);

		}
		;

		// First monomer after end of mutation.
		// The conformation(direction) of this momomer is not necessarily as
		// written in mutation conformation,
		// therefore we will calculate its direction according to position of 3
		// vectors.
		v1.set(mutationStartPos);
		v1.add(entry.positionOffsets.get(entry.positionOffsets.size() - 2));
		v2.set(originalProtein.get(mutationEndMonomer).getR());
		v3.set(originalProtein.get(mutationEndMonomer + 1).getR());
		newConformation.add(matrixMan.getDirection(v1, v2, v3));

		// Comformatino after mutation
		for (monomerNumber = mutationEndMonomer + 2; monomerNumber < size; monomerNumber++)
			newConformation.add(originalConformation.get(monomerNumber));

		temp = protein.setConformation(newConformation);

		if (temp < mutationStartMonomer)
			throw new RuntimeException(
			"This is weird. This structure was already tested in the past, how come it failes now?");
		if (temp < size) {
			numOfFailers++;
			return false;
		}

		return true;
	}

	/*
	 * private boolean activateMutationOnProtein(Protein protein, Protein
	 * originalProtein, MutationLibraryEntry entry,int mutationStartMonomer, int
	 * mutationEndMonomer) { numOfIterations++; protein.reset();
	 * protein.conformation.clear();
	 * 
	 * Monomer mon; for (int i = 0; i < mutationStartMonomer; i++){ mon =
	 * protein.get(i); mon.setR(new Vector3f(originalProtein.get(i).getR()));
	 * protein.getGrid().update(mon); }
	 * 
	 * Vector3f mutationStartPos =
	 * originalProtein.get(mutationStartMonomer).getR(); for (int i =
	 * mutationStartMonomer, j = 0; i <= mutationEndMonomer; i++, j++){ mon =
	 * protein.get(i); Vector3f vec = new
	 * Vector3f(entry.positionOffsets.get(j)); vec.add(mutationStartPos);
	 * mon.setR(vec); protein.getGrid().update(mon); }
	 * 
	 * for (int i = mutationEndMonomer + 1; i < protein.size(); i++){ mon =
	 * protein.get(i); mon.setR(new Vector3f(originalProtein.get(i).getR()));
	 * protein.getGrid().update(mon); } protein.updateFitness();
	 * protein.conformation.setFitness(protein.getFitness());
	 * protein.conformation.setEnergy(protein.getEnergy());
	 * protein.getGrid().reset(protein);
	 * 
	 * return true; }
	 */

	/**
	 * @param protein
	 *            - mutation will be created from this protein
	 * @param out
	 *            - protein with low fitness. if mutation process is successful
	 *            out will become the result of the mutation, else out will be
	 *            reset.
	 * @param max_tries
	 *            - max tries for creating a mutation.
	 */
	public void mutate(Protein protein, Protein out, int max_tries) {
		if (protein.size() < 10)
			throw new RuntimeException("A protein of length "+protein.size()+" Shorter than the predefined mutations");
		if (protein.getConformation().size() == 0)
			throw new RuntimeException("protein.conformation.size() == 0");
		int nTries = 0;
		boolean success = false;
		int mutationLength;
		int mutationStartMonomer;
		int mutationLastMonomer;
		Vector3f representativeVector = new Vector3f();
		Vector3f start, end;
		ArrayList<MutationLibraryEntry> list;
		if (originalConformationIn == null) // first time here
			originalConformationIn = new Conformation(protein.getConformation().size());
		if (originalConformationOut == null) // first time here
			originalConformationOut = new Conformation(protein.getConformation().size());
		int selectedMutationIndex;
		originalConformationIn.copy(protein.getConformation());
		originalConformationOut.copy(out.getConformation());
		if (originalConformationIn.size() == 0)
			throw new RuntimeException("originalConformation.size() == 0 ; protein.size() = "+protein.size()+"; protein.conformation.size() = "+protein.getConformation().size());
		MutationLibraryEntry entry;

		while (nTries < max_tries) { // upon success the method will return
			nTries++;
			out.reset();
			// Generate a number between [1..protein.size-2]
			mutationLength = random.nextInt(Math.min(protein.size() - 1,
					mutationLibraries.length) - 2) + 2;
			// the start monomer may be between 1 and protein size- mutation
			// length
			mutationStartMonomer = random.nextInt(protein.size()
					- mutationLength - 1) + 1;
			mutationLastMonomer = mutationLength + mutationStartMonomer - 1;
			start = protein.get(mutationStartMonomer).getR(); // get position
			// vector of start monomer.
			end = protein.get(mutationLastMonomer).getR(); // get position
			// vector of end monomer.
			representativeVector.sub(end, start);

			// if start monomer and end monomer are on a straight line no
			// mutation is possible.
			if (representativeVector.length() + 1 == mutationLength)
				continue;

			mutationLibrary = mutationLibraries[mutationLength];
			list = mutationLibrary.get(representativeVector);
			if (list == null) // list not found for given arguments
				throw new RuntimeException("ERROR: \nstart vector:" + start
						+ " \nend Vector" + end + "\nsub:"
						+ representativeVector + "\n" + "Mutation length:"
						+ mutationLength + "\n" + "Mutation Start Monomer:"
						+ mutationStartMonomer + "\n"
						+ "Mutation Last Monomer:" + mutationLastMonomer + "\n"
						+ "Protein:" + protein.toString());
			if (list.size() > 0) {

				/* Filter mutations - save only valid mutations */

				// collect position of the protein before and after the
				// mutation.
				// save them not as absolut position but as offset from the star
				// of mutation, this
				// helps us compare them to the position needed by the mutation.
				proteinPositions.clear();
				for (int i = 0; i < protein.size(); i++) {
					if ((i < mutationStartMonomer || i > mutationLastMonomer)) {
						// TODO: avoid creating a new vector.
						Vector3f vec = new Vector3f(protein.get(i).getR());
						vec.sub(start); // Save position as an offset from start
						// of mutation.
						proteinPositions.add(vec);
					}
				}
				// Save in candidates only the mutations that do no overlap with
				// already occupied position of the protein.
				// (the position collected in the previous loop)
				candidates.clear();
				for (MutationLibraryEntry lib : list) {
					boolean found = false;
					for (Vector3f vec : lib.positionOffsets) {
						if ((found = proteinPositions.contains(vec)))
							break;
					}
					if (!found)
						candidates.add(lib);
				}
				if (candidates.isEmpty()) // if no candidates try new mutation.
					continue;

				// Select random mutation from candidates.
				selectedMutationIndex = selectMutationNumber(candidates);
				entry = candidates.get(selectedMutationIndex);
				success = activateMutationOnProtein(out, protein, entry,
						mutationStartMonomer, mutationLastMonomer,
						originalConformationIn);

			}
			if (success)
				return;
		}
		out.reset();
		out.setConformation(originalConformationOut);
		
	}

	public int getNumOfFailures() {
		return numOfFailers;
	}

	public int getNumOfIterations() {
		return numOfIterations;
	}


}
