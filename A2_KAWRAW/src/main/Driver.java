package main;
import java.io.*;
import exceptions.*;

public class Driver {
	

	public static void main(String[] args) {
		
		// part 1’s manifest file
        String part1_manifest = "part1_manifest.txt";
        
        // Call do_part1 to partition movies
        String part2_manifest = do_part1(part1_manifest);

	}
	
	public static String do_part1(String manifestFile) {
        String part2_manifest = "part2_manifest.txt";

        BufferedWriter part2ManifestWriter = null;
        BufferedWriter badRecordsWriter = null;
        BufferedReader manifestReader = null;
        BufferedReader inputFileReader = null;
        BufferedWriter genreWriter = null;
        
        try {
            // Create output manifest file for part 2
            part2ManifestWriter = new BufferedWriter(new FileWriter(part2_manifest));

            // Create bad movie records file
            badRecordsWriter = new BufferedWriter(new FileWriter("bad_movie_records.txt"));

            // Read input file names from the manifest file
            manifestReader = new BufferedReader(new FileReader(manifestFile));
            String inputFileName;
            while ((inputFileName = manifestReader.readLine()) != null) {
                inputFileReader = new BufferedReader(new FileReader(inputFileName));
                String line;
                while ((line = inputFileReader.readLine()) != null) {
                    try {
                        // Parse and validate the movie record
                        Movie movie = parseMovieRecord(line);

                        // Write valid movie record to corresponding genre file
                        String genreFileName = movie.getGenres().toLowerCase() + ".csv";
                        genreWriter = new BufferedWriter(new FileWriter(genreFileName, true));
                        genreWriter.write(line + "\n");
                        genreWriter.close();

                        // Update part 2 manifest file
                        part2ManifestWriter.write(genreFileName + "\n");
                    } catch (BadYearException e) {
                        handleBadRecord(line, inputFileName, e, badRecordsWriter);
                    } catch (BadTitleException e) {
                        handleBadRecord(line, inputFileName, e, badRecordsWriter);
                    } catch (BadGenreException e) {
                        handleBadRecord(line, inputFileName, e, badRecordsWriter);
                    } catch (BadScoreException e) {
                        handleBadRecord(line, inputFileName, e, badRecordsWriter);
                    } catch (BadDurationException e) {
                        handleBadRecord(line, inputFileName, e, badRecordsWriter);
                    } catch (BadRatingException e) {
                        handleBadRecord(line, inputFileName, e, badRecordsWriter);
                    } catch (BadNameException e) {
                        handleBadRecord(line, inputFileName, e, badRecordsWriter);
                    } catch (MissingQuotesException e) {
                        handleBadRecord(line, inputFileName, e, badRecordsWriter);
                    } catch (ExcessFieldsException e) {
                        handleBadRecord(line, inputFileName, e, badRecordsWriter);
                    } catch (MissingFieldsException e) {
                        handleBadRecord(line, inputFileName, e, badRecordsWriter);
                    }
                }
                inputFileReader.close();
            }

            manifestReader.close();
            part2ManifestWriter.close();
            badRecordsWriter.close();
        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found - " + e.getMessage());
        }catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }

        return part2_manifest;
	}
	
	public static Movie parseMovieRecord(String record) throws BadYearException, BadTitleException, BadGenreException, BadScoreException, BadDurationException, BadRatingException, BadNameException, MissingQuotesException, ExcessFieldsException, MissingFieldsException{
        
		// Split record into fields
        String[] fields = record.split(",");

        // Check if number of fields is not 10
        if (fields.length != 10) {
            throw new MissingFieldsException("Missing or excess fields");
        }

        // Remove leading/trailing whitespace from fields
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }

        // Validate year
        int year;
        try {
            year = Integer.parseInt(fields[0]);
            if (year < 1990 || year > 1999) {
                throw new BadYearException("Invalid year");
            }
        } catch (NumberFormatException e) {
            throw new BadYearException("Invalid year");
        }

        // Validate title
        if (fields[1].isEmpty()) {
            throw new BadTitleException("Missing title");
        }

        // Validate duration
        int duration;
        try {
            duration = Integer.parseInt(fields[2]);
            if (duration < 30 || duration > 300) {
                throw new BadDurationException("Invalid duration");
            }
        } catch (NumberFormatException e) {
            throw new BadDurationException("Invalid duration");
        }

        // Validate genres
        String genres = fields[3];
        if (genres.isEmpty()) {
            throw new BadGenreException("Missing genre");
        }

        // Validate rating
        String rating = fields[4];
        String[] validRatings = {"PG", "Unrated", "G", "R", "PG-13", "NC-17"};
        boolean validRating = false;
        for (String valid : validRatings) {
            if (valid.equals(rating)) {
                validRating = true;
                break;
            }
        }
        if (!validRating) {
            throw new BadRatingException("Invalid rating");
        }

        // Validate score
        double score;
        try {
            score = Double.parseDouble(fields[5]);
            if (score < 0 || score > 10) {
                throw new BadScoreException("Invalid score");
            }
        } catch (NumberFormatException e) {
            throw new BadScoreException("Invalid score");
        }

        // Validate director
        if (fields[6].isEmpty()) {
            throw new BadNameException("Missing director");
        }

        // Validate actors
        for (int i = 7; i <= 9; i++) {
            if (fields[i].isEmpty()) {
                throw new BadNameException("Missing actor name");
            }
        }

        // Create and return movie object
        return new Movie(year, fields[1], duration, genres, rating, score, fields[6], fields[7], fields[8], fields[9]);
	}
	
	 // Method to handle bad record and write to bad movie records file
	public static void handleBadRecord(String line, String inputFileName, Exception e, BufferedWriter badRecordsWriter) throws IOException {
        badRecordsWriter.write("Error: " + e.getMessage() + " - Type: " + e.getClass().getSimpleName() + "\n");
        badRecordsWriter.write("Original Movie Record: " + line + "\n");
        badRecordsWriter.write("Input File: " + inputFileName + "\n\n");
    }
	
	

}
