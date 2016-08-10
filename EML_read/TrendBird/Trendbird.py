#-*- coding: utf-8 -*-
from email import message_from_file
import os
import fnmatch
from bs4 import BeautifulSoup
import csv

# Path to directory where attachments will be stored:
path = "./msgfiles"

# To have attachments extracted into memory, change behaviour of 2 following functions:

def file_exists (f):
    """Checks whether extracted file was extracted before."""
    return os.path.exists(os.path.join(path, f))

def save_file (fn, cont):
    """Saves cont to a file fn"""
    file = open(os.path.join(path, fn), "wb")
    file.write(cont)
    file.close()

def construct_name (id, fn):
    """Constructs a file name out of messages ID and packed file name"""
    id = id.split(".")
    id = id[0]+id[1]
    return id+"."+fn

def disqo (s):
    """Removes double or single quotations."""
    s = s.strip()
    if s.startswith("'") and s.endswith("'"): return s[1:-1]
    if s.startswith('"') and s.endswith('"'): return s[1:-1]
    return s

def disgra (s):
    """Removes < and > from HTML-like tag or e-mail address or e-mail ID."""
    s = s.strip()
    if s.startswith("<") and s.endswith(">"): return s[1:-1]
    return s

def pullout (m, key):
    """Extracts content from an e-mail message.
    This works for multipart and nested multipart messages too.
    m   -- email.Message() or mailbox.Message()
    key -- Initial message ID (some string)
    Returns tuple(Text, Html, Files, Parts)
    Text  -- All text from all parts.
    Html  -- All HTMLs from all parts
    Files -- Dictionary mapping extracted file to message ID it belongs to.
    Parts -- Number of parts in original message.
    """
    Html = ""
    Text = ""
    Files = {}
    Parts = 0
    if not m.is_multipart():
        if m.get_filename(): # It's an attachment
            fn = m.get_filename()
            cfn = construct_name(key, fn)
            Files[fn] = (cfn, None)
            if file_exists(cfn): return Text, Html, Files, 1
            save_file(cfn, m.get_payload(decode=True))
            return Text, Html, Files, 1
        # Not an attachment!
        # See where this belongs. Text, Html or some other data:
        cp = m.get_content_type()
        if cp=="text/plain": Text += m.get_payload(decode=True)
        elif cp=="text/html": Html += m.get_payload(decode=True)
        else:
            # Something else!
            # Extract a message ID and a file name if there is one:
            # This is some packed file and name is contained in content-type header
            # instead of content-disposition header explicitly
            cp = m.get("content-type")
            try: id = disgra(m.get("content-id"))
            except: id = None
            # Find file name:
            o = cp.find("name=")
            if o==-1: return Text, Html, Files, 1
            ox = cp.find(";", o)
            if ox==-1: ox = None
            o += 5; fn = cp[o:ox]
            fn = disqo(fn)
            cfn = construct_name(key, fn)
            Files[fn] = (cfn, id)
            if file_exists(cfn): return Text, Html, Files, 1
            save_file(cfn, m.get_payload(decode=True))
        return Text, Html, Files, 1
    # This IS a multipart message.
    # So, we iterate over it and call pullout() recursively for each part.
    y = 0
    while 1:
        # If we cannot get the payload, it means we hit the end:
        try:
            pl = m.get_payload(y)
        except: break
        # pl is a new Message object which goes back to pullout
        t, h, f, p = pullout(pl, key)
        Text += t; Html += h; Files.update(f); Parts += p
        y += 1
    return Text, Html, Files, Parts

def extract (msgfile, key):
    """Extracts all data from e-mail, including From, To, etc., and returns it as a dictionary.
    msgfile -- A file-like readable object
    key     -- Some ID string for that particular Message. Can be a file name or anything.
    Returns dict()
    Keys: from, to, subject, date, text, html, parts[, files]
    Key files will be present only when message contained binary files.
    For more see __doc__ for pullout() and caption() functions.
    """
    m = message_from_file(msgfile)
    From, To, Subject, Date = caption(m)
    Text, Html, Files, Parts = pullout(m, key)
    Text = Text.strip(); Html = Html.strip()
    msg = {"subject": Subject, "from": From, "to": To, "date": Date,
        "text": Text, "html": Html, "parts": Parts}
    if Files: msg["files"] = Files
    return msg

def caption (origin):
    """Extracts: To, From, Subject and Date from email.Message() or mailbox.Message()
    origin -- Message() object
    Returns tuple(From, To, Subject, Date)
    If message doesn't contain one/more of them, the empty strings will be returned.
    """
    Date = ""
    if origin.has_key("date"): Date = origin["date"].strip()
    From = ""
    if origin.has_key("from"): From = origin["from"].strip()
    To = ""
    if origin.has_key("to"): To = origin["to"].strip()
    Subject = ""
    if origin.has_key("subject"): Subject = origin["subject"].strip()
    return From, To, Subject, Date

def get_Contents(html, date):
    content_list = []
    title_list = []
    link_list = []
    class_list = []
    title_url = []
    for i in range(1,20):
        try:
            tmp_title = html.find_all(href="#"+str(i))[0].getText()
            if tmp_title is None:
                break
        except:
            break
        title_list.append(tmp_title)
        title_url.append(str(html.find_all(href="#"+str(i))[0]))

    tmp = html.find_all('a')
    for item in title_list:
        for i in range(0, len(tmp)):
            if title_url.count(str(tmp[i])) == 1:
                continue

            tmp_title = tmp[i].getText()
            if tmp_title == item:
                tmp_link = str(tmp[i])
                tmp_link = '=HYPERLINK(\"' + tmp_link.split("\"")[1] + "\")"
                link_list.append(tmp_link)
                break

    for i in range(0, len(title_list)):
        if title_list[i][0] == '(':
            tmp_class = title_list[i].split(")")[0][1:]
            class_list.append(tmp_class)
            title_list[i] = title_list[i].replace("("+tmp_class+") ", "")
        else:
            class_list.append(u"기타")

    for i in range(0, len(title_list)):
        contents = {'일자': date, '제목': title_list[i], '구분': class_list[i], 'URL': link_list[i]}
        content_list.append(contents)

    return content_list

def WriteDictToCSV(csv_file,csv_columns,dict_data):
    try:
        with open(csv_file, 'wb') as csvfile:
            csvfile.write(u'\ufeff'.encode('utf-8').strip())
            writer = csv.DictWriter(csvfile, fieldnames=csv_columns)
            writer.writeheader()
            for data in dict_data:
                writer.writerow({k: v.encode('utf-8').strip() for k, v in data.items()})
    except IOError as (errno, strerror):
            print("I/O error({0}): {1}".format(errno, strerror))
    return

#  Read eml file

final_list = []
current_dir = os.path.dirname(os.path.abspath(__file__))    #Get current Path
current_dir = current_dir.replace('\\', '/')
eml_dir = current_dir+"/eml/"
month_list = ["Jan", "Feb", "Mar", "Apr",
              "May", "Jun", "Jul", "Aug",
              "Sep", "Oct", "Nov", "Dec"]

file_list = [os.path.join(dirpath, f)
    for dirpath, dirnames, files in os.walk(eml_dir)
    for f in fnmatch.filter(files, '*.eml')]

for i in range(0, len(file_list)):
    file_list[i] = file_list[i].replace('\\', '/')
    #print (file_list[i].decode('utf-8').encode('utf-8'))
    #file_list[i] = unicode(file_list[i].decode('utf-8'), 'utf-8')
    file_list[i] = unicode(file_list[i], 'cp949').encode('utf-8')       # Unicode problem with Korean words...

for fileName in file_list:
    f = open(unicode(fileName, 'utf-8'), "rb")                          # Unicode problem with Korean words....
    eml_content = extract(f, f.name)
    text = eml_content['html']                      #Get html info
    date = eml_content['date'].split(" ")           #Get date info
    f.close()

    s = os.path.split(fileName)

    soup = BeautifulSoup(text, "lxml")

    month = str(month_list.index(date[2]) + 1)

    year = str(date[3])

    if date[1] < 10:
        day = '0' + str(date[1])
    else:
        day = str(date[1])

    date = year + '-' + month + '-' + day

    final_list += get_Contents(soup, date)

csv_columns = ['일자', '구분', '제목', 'URL']
currentPath = os.getcwd()
csv_file = "TrendBird"+".csv"

WriteDictToCSV(csv_file, csv_columns, final_list)