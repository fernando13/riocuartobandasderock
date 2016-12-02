#encoding: utf-8

require 'rest-client'
require 'json'
require "rspec"
include RSpec::Matchers

HOST = "localhost"
PORT = "7500"

def execute_sql(sql_code)
    done = system "sh db_execute.sh \"#{sql_code}\""
    raise Exception.new("Issue executing sql code: #{sql_code}") unless done
end

Given(/^that the application has been started$/) do
      # Application is started by the setUp routines
      # Nothing to do here...
end

Given(/^that the album's database is empty$/) do
    result = `psql -h #{HOST} -p #{PORT} -U rock_db_owner -d rcrockbands -c \"select count(*) from AlbumDB;\" -t`
    result = result.gsub(/[^[:print:]]|\s/,'') # removing non printable chars
    expect(result=="0")
end

Given(/^that the song's database is empty$/) do

    result = `psql -h #{HOST} -p #{PORT} -U rock_db_owner -d rcrockbands -c \"select count(*) from SongDB;\" -t`

    result = result.gsub(/[^[:print:]]|\s/,'') # removing non printable chars

    expect(result=="0")
end

Given(/^that the song's database have one song with name "([^"]*)" and duration "([^"]*)" and belongs to the album "([^"]*)"$/) do |name, duration, title|
  `psql -h #{HOST} -p #{PORT}  -U rock_db_owner -d rcrockbands -c \"INSERT INTO BandDB VALUES ('7','nam','Reggae');\" -t`
  `psql -h #{HOST} -p #{PORT}  -U rock_db_owner -d rcrockbands -c \"INSERT INTO AlbumDB VALUES ('1','#{title}','2010-12-27','7');\" -t`
  response = RestClient.post 'http://localhost:4567/songs/', { :name => name, :duration => duration , :albumTitle => title}, :content_type => 'text/plain'
  expect(response.code).to eq(201)
end

Given(/^that the song's database have one song with UUID "([^"]*)"$/) do |id|
    `psql -h #{HOST} -p #{PORT} -U rock_db_owner -d rcrockbands -c \"INSERT INTO SongDB VALUES ('#{id}','Jijiji',400);\" -t`
end

## update feature
Given(/that the song's database have one song with UUID "([^"]*)" and name "([^"]*)" and duration "([^"]*)" and belongs to the album "([^"]*)"$/) do |id, name, duration, title|
    
	`psql -h #{HOST} -p #{PORT}  -U rock_db_owner -d rcrockbands -c \"INSERT INTO BandDB VALUES ('7','nam','Reggae');\" -t`
  	`psql -h #{HOST} -p #{PORT}  -U rock_db_owner -d rcrockbands -c \"INSERT INTO AlbumDB VALUES ('1','#{title}','2010-12-27','7');\" -t`
	`psql -h #{HOST} -p #{PORT} -U rock_db_owner -d rcrockbands -c \"INSERT INTO SongDB VALUES ('#{id}','#{name}','#{duration}');\" -t`
end

## update feature 1
Given(/that the song's database is empty and exist an album "([^"]*)"$/) do |title|
    
	`psql -h #{HOST} -p #{PORT}  -U rock_db_owner -d rcrockbands -c \"INSERT INTO BandDB VALUES ('7','nam','Reggae');\" -t`
  	`psql -h #{HOST} -p #{PORT}  -U rock_db_owner -d rcrockbands -c \"INSERT INTO AlbumDB VALUES ('1','#{title}','2010-12-27','7');\" -t`
end

Given(/^that the album's database contains an album named "([^"]*)" with the song "([^"]*)"$/) do |albumName, songName|
  pending # Write code here that turns the phrase above into concrete actions
end

Given(/^that the artist's database have one artist with name "([^"]*)" and surname "([^"]*)" and nickname "([^"]*)"$/) do |name,surname,nickname|
  response = RestClient.post 'http://localhost:4567/artist/', { :name => name, :surname => surname, :nickname => nickname }, :content_type => 'text/plain'
  expect(response.code).to eq(201)
end

Given(/^that the album's database have one album with title "([^"]*)" and release date "([^"]*)"$/) do |title, release_date|
  response = RestClient.post 'http://localhost:4567/albums', { :title => title, :release_date => release_date}, :content_type => 'text/plain'
  expect(response.code).to eq(201)
end

Given(/^that the album's database contains an album named "([^"]*)" with release date "([^"]*)"$/) do |title,release_date|
  response = RestClient.post 'http://localhost:4567/albums', { :title => title, :release_date => release_date }, :content_type => 'text/plain'
  expect(response.code).to eq(201)
end

Given(/^that the album's database contains an album with title "([^"]*)" and release date "([^"]*)"$/) do |currentTitle, currentReleaseDate|
   response = RestClient.post 'http://localhost:4567/albums', { :title => currentTitle, :release_date => currentReleaseDate }, :content_type => 'text/plain'
   expect(response.code).to eq(201)
end

Given(/^that the album's database have (\d+) entries$/) do |numEntries|
    1..numEntries.to_i.times do |n|
        response = RestClient.post "http://localhost:4567/albums", { :title => "Album#{n}" }, :content_type => 'text/plain'
    end
    result = `psql -h #{HOST} -p #{PORT} -U rock_db_owner -d rcrockbands -c \"select count(*) from AlbumDB;\" -t`
    result = result.gsub(/[^[:print:]]|\s/,'')
    expect(result == numEntries)
end

Given(/^the album named "([^"]*)" doesn't exist in database$/) do |title|
    queryResult = `psql -h #{HOST} -p #{PORT} -U rock_db_owner -d rcrockbands -c \"select count(*) from AlbumDB where title = '#{title}';\" -t`
    queryResult = queryResult.gsub(/[^[:print:]]|\s/,'')
    expect(queryResult == "0")
end


When(/^I try to add an album with name "([^"]*)" and release date "([^"]*)"$/) do |title,release_date|
  begin
  response = RestClient.post 'http://localhost:4567/albums', { :title => title, :release_date => release_date}, :content_type => 'text/plain'
  expect(response.code).to eq(201)
  rescue RestClient::Conflict => e
  rescue => e
    expect(e.response.code).to eq(400)
  end
end

Then(/^the system informs that the album named "([^"]*)" with release date "([^"]*)" already exists in the database$/) do |title,release_date|
  resultingTitle = `psql -h #{HOST} -p #{PORT}  -U rock_db_owner -d rcrockbands -c \"select title from AlbumDB;\" -t`
  resultingTitle = resultingTitle.gsub(/[^[:print:]]|\s/,'') # removing non printable chars
  expect(resultingTitle == title)
  resultingReleaseDate = `psql -h #{HOST} -p #{PORT}  -U rock_db_owner -d rcrockbands -c \"select releaseDate from AlbumDB;\" -t`
  resultingReleaseDate = resultingReleaseDate.gsub(/[^[:print:]]|\s/,'') # removing non printable chars
  expect(resultingReleaseDate == release_date)
end

And(/^the album's database does not change and maintain (\d+) entry$/) do |entry|
  queryResult = `psql -h #{HOST} -p #{PORT}  -U rock_db_owner -d rcrockbands -c \"select count(*) from AlbumDB;\" -t`
  queryResult = queryResult.gsub(/[^[:print:]]|\s/,'') # removing non printable chars
  expect(queryResult == entry)
end

When(/^I add an album with name "([^"]*)" and release date "([^"]*)"$/) do |title,release_date|

  response = RestClient.post 'http://localhost:4567/albums', { :title => title, :release_date => release_date }, :content_type => 'text/plain'
  expect(response.code).to eq(201)

end

When(/^I search an album with "([^"]*)" "([^"]*)" , the result of the search should have (\d+) entry$/) do |atributo, valor, entradas|
  begin
    String s = 'http://localhost:4567/albums/findby' + atributo + '/' + valor
    response = RestClient.get s
    if entradas != "0"
      expect(response.code).to eq(200)
    else
      expect(response.code).to eq(204)
    end
  rescue RestClient::NotFound => e
    expect(entradas == "")
  end
end

When(/^I add a song with name "([^"]*)" and duration "([^"]*)" and belongs to the album "([^"]*)"$/) do |name, duration, title|
 begin
    `psql -h #{HOST} -p #{PORT}  -U rock_db_owner -d rcrockbands -c \"INSERT INTO BandDB VALUES ('10','name','Rock');\" -t`
    `psql -h #{HOST} -p #{PORT}  -U rock_db_owner -d rcrockbands -c \"INSERT INTO AlbumDB VALUES ('2','#{title}','2000-12-27','10');\" -t`
  
     response = RestClient.post 'http://localhost:4567/songs/', { :name => name, :duration => duration, :albumTitle => title }, :content_type => 'text/plain'
     expect(response.code).to eq(201)
     rescue RestClient::Conflict => e
           rescue => e
             expect(e.response.code).to eq(400)
     end
end

When(/^I try to add a song with name "([^"]*)" and duration "([^"]*)"$/) do |name, duration|
 begin
     response = RestClient.post 'http://localhost:4567/songs/', { :name => name, :duration => duration}, :content_type => 'text/plain'
     expect(response.code).to eq(201)
   rescue RestClient::Conflict => e
    rescue => e
      expect(e.response.code).to eq(400)
  end
end

When(/^I search a song with name "([^"]*)" , the result of the search should have (\d+) entry$/) do |value, entries|
  begin
      String s = 'http://localhost:4567/songs/findbyname/'+value.gsub(/[^[:print:]]|\s/,'')
      response = RestClient.get s
      if entries != "0"
        expect(response.code).to eq(200)
      else
        expect(response.code).to eq(204)
      end
    rescue RestClient::Conflict => e
        expect(e.response.code).to eq(400)
    end
end

When(/^I search a song with duration "([^"]*)" , the result of the search should have (\d+) entry$/) do |value, entries|
  begin
      String s = 'http://localhost:4567/songs/findbyduration/'+ value
      response = RestClient.get s
      if entries != "0"
        expect(response.code).to eq(200)
      else
        expect(response.code).to eq(204)
      end
    rescue RestClient::Conflict => e
        expect(e.response.code).to eq(400)
    end
end

When(/^the entry should have name "([^"]*)", duration "([^"]*)" and belongs to the album "([^"]*)"$/) do |arg1, arg2, arg3|
  pending # Write code here that turns the phrase above into concrete actions
end

When(/^I list all the albums the result of the search should have (\d+) entries$/) do |arg1|
  begin
    response = RestClient.get "http://localhost:4567/albums"
    puts("Response: "+response)
    if (arg1 == "0")
      expect(response.code).to eq(204)
    else
      expect(response.code).to eq(200)
    end
  end
end


When(/^I update the album name from "([^"]*)" to "([^"]*)" keeping "([^"]*)" as release date$/) do |oldTitle, newTitle, releaseDate|
    begin
        queryResult = `psql -h #{HOST} -p #{PORT} -U rock_db_owner -d rcrockbands -c \"select albumID from AlbumDB where title = '#{oldTitle}' and releaseDate = '#{releaseDate}';\" -t`
        queryResult = queryResult.gsub(/[^[:print:]]|\s/,'')
	puts("http://localhost:4567/albums/#{queryResult}")
        response = RestClient.put "http://localhost:4567/albums/#{queryResult}", { :title => newTitle, :release_date => releaseDate }, :content_type => 'text/plain'
        expect(response.code).to eq(201)
        rescue RestClient::Conflict => e
            expect(response.code).to eq(409)
        end
end

When(/^I update the album release date from "([^"]*)" to "([^"]*)" keeping "([^"]*)" as name$/) do |oldReleaseDate, newReleaseDate, title|
    begin
        queryResult = `psql -h #{HOST} -p #{PORT} -U rock_db_owner -d rcrockbands -c \"select albumID from AlbumDB where title = '#{title}' and releaseDate = '#{oldReleaseDate}';\" -t`
        queryResult = queryResult.gsub(/[^[:print:]]|\s/,'')
        puts("http://localhost:4567/albums/#{queryResult}")
	response = RestClient.put "http://localhost:4567/albums/#{queryResult}", { :title => title, :release_date => newReleaseDate }, :content_type => 'text/plain'
        expect(response.code).to eq(201)
        rescue RestClient::Conflict => e
            expect(response.code).to eq(409)
        end
end

When(/^I update the album with name "([^"]*)" and release date "([^"]*)" to name "([^"]*)" and release date "([^"]*)"$/) do |oldTitle, oldReleaseDate, newTitle, newReleaseDate|
    begin
        queryResult = `psql -h #{HOST} -p #{PORT} -U rock_db_owner -d rcrockbands -c \"select albumID from AlbumDB where title = '#{oldTitle}' and releaseDate = '#{oldReleaseDate}';\" -t`
        queryResult = queryResult.gsub(/[^[:print:]]|\s/,'')
	puts("http://localhost:4567/albums/#{queryResult}")
        response = RestClient.put "http://localhost:4567/albums/#{queryResult}", { :title => newTitle, :release_date => newReleaseDate }, :content_type => 'text/plain'
        expect(response.code).to eq(201)
        rescue RestClient::Conflict => e
            expect(response.code).to eq(409)
        end
end


# update feature 1
When(/^I update a song with UUID "([^"]*)" and name "([^"]*)" and duration "([^"]*)" and album "([^"]*)"$/) do |uuid, name, duration, albumName|
begin
  
    response = RestClient.put "http://localhost:4567/songs/#{uuid}", { :name => name, :duration => duration, :albumTitle => albumName }, :content_type => 'text/plain'
            expect(response.code).to eq(200)
        rescue RestClient::Conflict => e
            expect(response.code).to eq(409)
        end

end

# update feature 3
When(/^I update the song with UUID "([^"]*)" and name "([^"]*)" to duration "([^"]*)" and album "([^"]*)"$/) do |uuid, name, newDuration, albumName|
    begin
	response = RestClient.put "http://localhost:4567/songs/#{uuid}", {:name => name, :duration => newDuration, :albumTitle => albumName}, :content_type => 'text/plain'
        expect(response.code).to eq(200)
        rescue RestClient::Conflict => e
            expect(response.code).to eq(409)
        end
end

# update feature 2 y 4
When(/^I update the song with UUID "([^"]*)" to name "([^"]*)" and duration "([^"]*)" and album "([^"]*)"$/) do |uuid, name, duration, albumName|
    begin
        response = RestClient.put "http://localhost:4567/songs/#{uuid}", {:name => name, :duration => duration, :albumTitle => albumName}, :content_type => 'text/plain'
        expect(response.code).to eq(200)
        rescue RestClient::Conflict => e
            expect(response.code).to eq(409)
        end
end


When(/^I delete a song with UUID "([^"]*)"$/) do |id|
  begin
       String s = 'http://localhost:4567/songs/'+id
       response = RestClient.delete s
      rescue RestClient::Conflict => e
          expect(e.response.code).to eq(409)
      end
   end


Then(/^the artist's database should have (\d+) entry$/) do |arg1|
    result = `psql -h #{HOST} -p #{PORT}  -U rock_db_owner -d rcrockbands -c \"select count(*) from artistDB;\" -t`
    result = result.gsub(/[^[:print:]]|\s/,'') # removing non printable chars
    expect(result).to eq(arg1)
end

Then(/^the album's database should have (\d+) entry$/) do |arg1|
    result = `psql -h #{HOST} -p #{PORT}  -U rock_db_owner -d rcrockbands -c \"select count(*) from AlbumDB;\" -t`
    result = result.gsub(/[^[:print:]]|\s/,'') # removing non printable chars
    expect(result).to eq(arg1)
end

Then(/^the song's database should have (\d+) entry$/) do |arg1|
    result = `psql -h #{HOST} -p #{PORT}  -U rock_db_owner -d rcrockbands -c \"select count(*) from SongDB;\" -t`
    result = result.gsub(/[^[:print:]]|\s/,'') # removing non printable chars
    expect(result).to eq(arg1)
end


Then(/^the entry should have name "([^"]*)" and release date "([^"]*)"$/) do |title,release_date|
    resultingTitle = `psql -h #{HOST} -p #{PORT}  -U rock_db_owner -d rcrockbands -c \"select title from AlbumDB;\" -t`
    resultingTitle = resultingTitle.gsub(/[^[:print:]]|\s/,'') # removing non printable chars
    expect(resultingTitle).to eq(title)
    resultingReleaseDate = `psql -h #{HOST} -p #{PORT}  -U rock_db_owner -d rcrockbands -c \"select releaseDate from AlbumDB;\" -t`
    resultingReleaseDate = resultingReleaseDate.gsub(/[^[:print:]]|\s/,'') # removing non printable chars
    expect(resultingReleaseDate) == (release_date)
end

Then(/^the entry should have name "([^"]*)" and duration "([^"]*)"$/) do |name, duration|
    resultingName = `psql -h #{HOST} -p #{PORT}  -U rock_db_owner -d rcrockbands -c \"select name from SongDB;\" -t`
    resultingName = resultingName.gsub(/[^[:print:]]|\s/,'') # removing non printable chars
    expect(resultingName) == (name)
    resultingDuration = `psql -h #{HOST} -p #{PORT}  -U rock_db_owner -d rcrockbands -c \"select duration from SongDB;\" -t`
    resultingDuration = resultingDuration.gsub(/[^[:print:]]|\s/,'') # removing non printable chars
    expect(resultingDuration) == (duration)
end  


# updateSong feature
Then(/^the song's database should have one song with UUID "([^"]*)", name "([^"]*)" and duration "([^"]*)" exist in database$/) do |uuid, name, duration|
	resultingUuid = `psql -h #{HOST} -p #{PORT}  -U rock_db_owner -d rcrockbands -c \"select idsong from SongDB;\" -t`   
	resultingUuid = resultingUuid.gsub(/[^[:print:]]|\s/,'') # removing non printable chars
    expect(resultingUuid) == (uuid)
    resultingName = `psql -h #{HOST} -p #{PORT}  -U rock_db_owner -d rcrockbands -c \"select name from SongDB;\" -t`
    resultingName = resultingName.gsub(/[^[:print:]]|\s/,'') # removing non printable chars
    expect(resultingName) == (name)
    resultingDuration = `psql -h #{HOST} -p #{PORT}  -U rock_db_owner -d rcrockbands -c \"select duration from SongDB;\" -t`
    resultingDuration = resultingDuration.gsub(/[^[:print:]]|\s/,'') # removing non printable chars
    expect(resultingDuration) == (duration)
end

Then(/^the album's database remains empty$/) do
    result = `psql -h #{HOST} -p #{PORT} -U rock_db_owner -d rcrockbands -c \"select count(*) from AlbumDB;\" -t`
    result = result.gsub(/[^[:print:]]|\s/,'')
    expect(result).to eq("0")
end


Then(/^the album's database contains an album named "([^"]*)" with release date "([^"]*)"$/) do |title, releaseDate|
    queryResult = `psql -h #{HOST} -p #{PORT} -U rock_db_owner -d rcrockbands -c \"select count(*) from AlbumDB where title = '#{title}' and releaseDate = '#{releaseDate}';\" -t`
    queryResult = queryResult.gsub(/[^[:print:]]|\s/,'')
    expect(queryResult == "1")
end




#------------------------------------------------------------------

When(/^I try to delete an album with name "([^"]*)" and release date "([^"]*)"$/) do |title, releaseDate|
  begin
  response = RestClient.post 'http://localhost:4567/albums', { :title => title, :release_date => releaseDate}, :content_type => 'text/plain'
  queryResult = `psql -h #{HOST} -p #{PORT}  -U rock_db_owner -d rcrockbands -c \"select albumID from AlbumDB;\" -t`
  queryResult = queryResult.gsub(/[^[:print:]]|\s/,'')
  response = RestClient.delete "http://localhost:4567/albums/#{queryResult}"
  expect(response.code).to eq(201)
  response = RestClient.delete "http://localhost:4567/albums/#{queryResult}"
  rescue RestClient::Conflict => e
    expect(e.response.code).to eq(409)
  end
end

Given(/^that the album's database have (\d+) entry$/) do |number|
  $i = 0
  while $i < number.to_i do
     newTitle = "Encontrados#$i"
     response = RestClient.post 'http://localhost:4567/albums', { :title => '#{newTitle}'}, :content_type => 'text/plain'
     $i +=1
  end
  queryResult = `psql -h #{HOST} -p #{PORT}  -U rock_db_owner -d rcrockbands -c \"select count(*) from AlbumDB;\" -t`
  queryResult = queryResult.gsub(/[^[:print:]]|\s/,'') # removing non printable chars
  expect(queryResult == number.to_i)
end


Given(/^the album named "([^"]*)" doesn't exist in the album's database$/) do |title|
    queryResult = `psql -h #{HOST} -p #{PORT} -U rock_db_owner -d rcrockbands -c \"select count(*) from AlbumDB where title = '#{title}';\" -t`
    queryResult = queryResult.gsub(/[^[:print:]]|\s/,'')
    expect(queryResult == "0")
end

When(/^I delete the album named "([^"]*)"$/) do |title|
 begin
  response = RestClient.post 'http://localhost:4567/albums', { :title => title}, :content_type => 'text/plain'
  queryResult = `psql -h #{HOST} -p #{PORT}  -U rock_db_owner -d rcrockbands -c \"select albumID from AlbumDB where title = '#{title}';\" -t`
  queryResult = queryResult.gsub(/[^[:print:]]|\s/,'')
  response = RestClient.delete "http://localhost:4567/albums/#{queryResult}"
  expect(response.code).to eq(201)
  response = RestClient.delete "http://localhost:4567/albums/#{queryResult}"
  rescue RestClient::Conflict => e
    expect(e.response.code).to eq(409)
  end
end

Then(/^the album's database have (\d+) entry$/) do |number|
  queryResult = `psql -h #{HOST} -p #{PORT}  -U rock_db_owner -d rcrockbands -c \"select count(*) from AlbumDB;\" -t`
  queryResult = queryResult.gsub(/[^[:print:]]|\s/,'') # removing non printable chars
  expect(queryResult == number.to_i)
end

Given(/^that the database contains an album with name "([^"]*)" and release date "([^"]*)"$/) do |title, releaseDate|
  response = RestClient.post 'http://localhost:4567/albums', { :title => title, :release_date => releaseDate}, :content_type => 'text/plain'
  queryResult = `psql -h #{HOST} -p #{PORT}  -U rock_db_owner -d rcrockbands -c \"select count(*) from AlbumDB;\" -t`
  queryResult = queryResult.gsub(/[^[:print:]]|\s/,'') # removing non printable chars
  expect(queryResult == "1")
end

When(/^I delete the album with name "([^"]*)" and release date "([^"]*)"$/) do |title, releaseDate|
  queryResult = `psql -h #{HOST} -p #{PORT} -U rock_db_owner -d rcrockbands -c \"select albumID from AlbumDB where title = '#{title}' and releaseDate = '#{releaseDate}';\" -t`
  queryResult = queryResult.gsub(/[^[:print:]]|\s/,'')
  response = RestClient.delete "http://localhost:4567/albums/#{queryResult}"
  expect(response.code).to eq(201)
end

Then(/^the album's database doesn't contain an album with name "([^"]*)" and release date "([^"]*)"$/) do |title, releaseDate|
  queryResult = `psql -h #{HOST} -p #{PORT} -U rock_db_owner -d rcrockbands -c \"select count(*) from AlbumDB where title = '#{title}' and releaseDate = '#{releaseDate}';\" -t`
  queryResult = queryResult.gsub(/[^[:print:]]|\s/,'')
  expect(queryResult == "0")
end
