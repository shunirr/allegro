#!/usr/bin/env ruby
# -*- coding: utf-8 -*-
require 'sinatra'
require 'json'

get '/' do 
  content_type :json
  status 200

  data = Dir.glob("*.apk").map do |f|
    file = File.new f
    name = f.split("/")[-1].gsub(/\.apk$/, '')
    {
      'title' => name,
      'uri' => "http://example.com/#{name}.apk",
      'last_modified' => file.ctime.to_s,
      'size' => file.size,
    }
  end

  JSON.unparse data
end

